package com.guneet.atb.config;


import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import java.time.Duration;

@Configuration
@EnableR2dbcRepositories(basePackages = "com/guneet/atb/model/rds/repositories/writer",
        entityOperationsRef = "writerR2dbcEntityTemplate")
public class MasterDatabaseConfig {

    @Value("${spring.r2dbc.master.url}")
    private String dbURL;

    @Value("${spring.r2dbc.master.pool.min-idle}")
    Integer minIdlePoolSize;

    @Value("${spring.r2dbc.master.pool.max-size}")
    Integer maxPoolSize;

    @Value("${spring.r2dbc.pool.max-idle-timeout}")
    Integer maxIdleTimeout;

    @Value("${spring.r2dbc.pool.max-life-time}")
    Integer maxLifeTime;

    @Value("${spring.r2dbc.pool.validation-query}")
    String validationQuery;

    @Bean(name = "writerConnectionFactory")
    public ConnectionFactory primaryConnectionFactory() {
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

    @Bean(name = "writerR2dbcEntityTemplate")
    public R2dbcEntityTemplate writerR2dbcEntityTemplate(@Qualifier(value = "writerConnectionFactory") ConnectionFactory connectionFactory) {
        return new R2dbcEntityTemplate(connectionFactory);
    }
}
