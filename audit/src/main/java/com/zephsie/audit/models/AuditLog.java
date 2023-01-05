package com.zephsie.audit.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@NoArgsConstructor
@Document(collection = "audit")
public class AuditLog {

    @Id
    @Getter
    @Setter
    private UUID id;

    @Getter
    @Setter
    private UUID userId;

    @Getter
    @Setter
    private String type;

    @Getter
    @Setter
    private String description;
}