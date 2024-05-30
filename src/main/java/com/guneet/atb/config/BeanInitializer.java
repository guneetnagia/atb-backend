package com.guneet.atb.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.time.Clock;
import java.time.LocalDateTime;

@Configuration
public class BeanInitializer {

    @Value("${command-control.url}")String commandControlUrl;
    @Value("${swagger_context}")String swaggerContext;

    @Value("${atb-internal.url}")String atbInternalUrl;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // SqlIdentifier Deserializer
        SimpleModule sqlIdentifierModule = new SimpleModule()
                .addKeyDeserializer(SqlIdentifier.class, new SqlIdentifierKeyDeserializer());
        objectMapper.registerModule(sqlIdentifierModule);

        // Jackson Module
        SimpleModule timeModule = new JavaTimeModule();
        timeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        objectMapper.registerModule(timeModule);

        // Jackson Module
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(LocalDateTime.class, new CustomLocalDateTimeDeserializer());
        objectMapper.registerModule(simpleModule);

        return objectMapper;
    }

    @Bean
    public WebProperties.Resources resources() {
        return new WebProperties.Resources();
    }

    @Bean
    public WebClient commandControlWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .clone()
                .baseUrl(commandControlUrl)
                .build();
    }

    @Bean
    public WebClient atbServiceWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .clone()
                .baseUrl(atbInternalUrl)
                .build();
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("source-info-header")
                .addOpenApiCustomizer(openApi -> {
                    Server server = openApi.getServers().get(0);
                    server.setUrl(server.getUrl() + swaggerContext);
                })
                .addOperationCustomizer((operation, $) -> {
                    operation.addParametersItem(
                            new HeaderParameter()
                                    .name("source-app")
                                    .description("source-app, if missing request will be FORBIDDEN")
                                    .required(true)
                    );
                    return operation;
                })
                .build();
    }

    @Bean
    RouterFunction<ServerResponse> staticResourceRouter(){
        return RouterFunctions.resources("/**", new ClassPathResource("static/"));
    }

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}

