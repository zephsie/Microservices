package com.zephsie.wellbeing.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuditLogDTO {
    private UUID id;

    private UUID userId;

    private String type;

    private String description;
}