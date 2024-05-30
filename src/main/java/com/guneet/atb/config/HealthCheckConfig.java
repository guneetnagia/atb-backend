package com.guneet.atb.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "healthcheck")
@Getter
@Setter
public class HealthCheckConfig {
    private String status;
    private String version;
    private String deployTime;
    private String opsTicket;
}
