package com.guneet.atb.config;


import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.util.StreamUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.Duration;

@Configuration
@EnableR2dbcRepositories(basePackages = "com/guneet/atb/model/rds/repositories/reader",
        entityOperationsRef = "readerR2dbcEntityTemplate")
public class SlaveDatabaseConfig {

    @Value("${spring.profiles.active}")
    String profile;

    @Value("${spring.r2dbc.slave.url}")
    private String dbURL;

    @Value("${spring.r2dbc.slave.pool.min-idle}")
    Integer minIdlePoolSize;

    @Value("${spring.r2dbc.slave.pool.max-size}")
    Integer maxPoolSize;

    @Value("${spring.r2dbc.pool.max-idle-timeout}")
    Integer maxIdleTimeout;

    @Value("${spring.r2dbc.pool.max-life-time}")
    Integer maxLifeTime;

    @Value("${spring.r2dbc.pool.validation-query}")
    String validationQuery;

    @Bean(name = "readerConnectionFactory")
    public ConnectionFactory readerConnectionFactory() {
        ConnectionFactory connectionFactory = ConnectionFactoryBuilder.withUrl(dbURL).build();
        ConnectionPoolConfiguration configuration = ConnectionPoolConfiguration.builder(connectionFactory)
                .minIdle(minIdlePoolSize)
                .maxSize(maxPoolSize)
                .maxIdleTime(Duration.ofMinutes(maxIdleTimeout))
                .maxLifeTime(Duration.ofMinutes(maxLifeTime))
                .validationQuery(validationQuery)
                .build();
        return new ConnectionPool(configuration);
    }

    @Bean(name = "readerR2dbcEntityTemplate")
    public R2dbcEntityTemplate readerR2dbcEntityTemplate(
            @Qualifier(value = "readerConnectionFactory") ConnectionFactory connectionFactory) throws IOException {
        R2dbcEntityTemplate dbcEntityTemplate = new R2dbcEntityTemplate(connectionFactory);
        if("test".equals(profile)) {
            try (InputStream taskTypeFileStream = new FileInputStream("src/test/resources/data/rds/task_types.sql")) {
                String schemaTaskTypes = StreamUtils.copyToString(
                        taskTypeFileStream,
                        Charset.defaultCharset());
                dbcEntityTemplate.getDatabaseClient().sql(schemaTaskTypes).fetch().rowsUpdated().block();
            }

            try (InputStream taskFileStream = new FileInputStream("src/test/resources/data/rds/tasks.sql")) {
                String schemaTasks = StreamUtils.copyToString(
                        taskFileStream,
                        Charset.defaultCharset());
                dbcEntityTemplate.getDatabaseClient().sql(schemaTasks).fetch().rowsUpdated().block();
            }
        }
        return dbcEntityTemplate;
    }
}
