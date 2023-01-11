package com.zephsie.audit.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor
@Document(collection = "audit")
public class AuditLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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