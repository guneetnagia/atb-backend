package com.guneet.atb.config.model;


import lombok.Getter;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mongodb")
@Getter
public class CustomMongoProperties {
    MongoProperties info = new MongoProperties();
}
