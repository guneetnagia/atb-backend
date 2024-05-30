package com.guneet.atb.controller;


import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.guneet.atb.config.HealthCheckConfig;

import java.util.HashMap;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Log4j2
@RestController
@Tag(name = "HealthCheckController", description = "Task Service Heath Monitoring")
@RequestMapping(path = "/", produces = APPLICATION_JSON_VALUE)
public class HealthCheckController {
    @Autowired
    HealthCheckConfig healthCheckConfig;

    @Operation(hidden = true, summary = "This method returns service deployment configuration based on Env variables")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Deployment configuration for the service",
            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = HashMap.class))})})

    @GetMapping(value = "/healthcheck")
    public ResponseEntity<HashMap<String, String>> healthCheck() {
        HashMap<String, String> healthCheckDetails = new HashMap<>();
        healthCheckDetails.put("DEPLOY_TIME", healthCheckConfig.getDeployTime());
        healthCheckDetails.put("OPS_TICKET", healthCheckConfig.getOpsTicket());
        healthCheckDetails.put("STATUS", healthCheckConfig.getStatus());
        healthCheckDetails.put("VERSION", healthCheckConfig.getVersion());
        return ResponseEntity.ok(healthCheckDetails);
    }

    @GetMapping(value = "/checkredis/{key}")
    public ResponseEntity<String> redisCheck(@PathVariable("key") String key) {
        // Connection URL to the Redis server. Format: redis://[password@]host[:port][/databaseNumber]
        String redisUrl = "redis://xyz.cache.amazonaws.com:6379";

        // Create a RedisClient
        RedisClient redisClient = RedisClient.create(redisUrl);

        // Establish a connection to Redis
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {

            // Obtain a synchronous command API
            RedisCommands<String, String> syncCommands = connection.sync();

            // Read the string back from Redis
            String getValue = syncCommands.get(key);
            return ResponseEntity.ok(getValue);

        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return ResponseEntity.ok(ExceptionUtils.getStackTrace(e));
        } finally {
            // Close the RedisClient
            redisClient.shutdown();
        }
    }
}

