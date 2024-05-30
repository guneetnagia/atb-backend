package com.guneet.atb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(basePackages = "com.guneet.atb.model.mongo.info", reactiveMongoTemplateRef = "infoMongoClient")
public class InfoMongoConfig {
}
