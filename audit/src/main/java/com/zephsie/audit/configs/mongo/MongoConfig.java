package com.zephsie.audit.configs.mongo;

import com.zephsie.audit.models.AuditLog;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;

import java.util.UUID;

@Configuration
public class MongoConfig {

    @Bean
    public BeforeConvertCallback<AuditLog> beforeSaveCallback() {
        return (entity, collection) -> {
            if (entity.getId() == null) {
                entity.setId(UUID.randomUUID());
            }
            return entity;
        };
    }
}