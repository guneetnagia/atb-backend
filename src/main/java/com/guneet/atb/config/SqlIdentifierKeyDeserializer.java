package com.guneet.atb.config;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.relational.core.sql.SqlIdentifier;

@Configuration
public class SqlIdentifierKeyDeserializer extends KeyDeserializer {
    @Override
    public SqlIdentifier deserializeKey(String key, DeserializationContext ctxt) {
        return SqlIdentifier.unquoted(key);
    }
}
