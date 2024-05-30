package com.guneet.atb.config;

import com.guneet.atb.config.model.CustomMongoProperties;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

@Configuration
@EnableConfigurationProperties(CustomMongoProperties.class)
@RequiredArgsConstructor
public class MongoConfig {
    private final CustomMongoProperties customMongoProperties;

    @Bean(name = "infoMongoClient")
    ReactiveMongoTemplate infoDBTemplate() {
        MongoProperties properties = this.customMongoProperties.getInfo();
        MongoClient mongoClient = MongoClients.create(properties.getUri());
        ReactiveMongoTemplate mongoTemplate = new ReactiveMongoTemplate(mongoClient, properties.getDatabase());
        ((MappingMongoConverter)mongoTemplate.getConverter()).setTypeMapper(new DefaultMongoTypeMapper(null));
        return mongoTemplate;
    }
}
