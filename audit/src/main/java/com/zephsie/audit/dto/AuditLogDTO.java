package com.zephsie.audit.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuditLogDTO {

    @NotNull
    private UUID userId;

    @NotNull
    private String type;

    @NotNull
    private String description;
}