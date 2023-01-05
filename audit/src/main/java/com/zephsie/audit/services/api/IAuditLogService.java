package com.zephsie.audit.services.api;

import com.zephsie.audit.dto.AuditLogDTO;
import com.zephsie.audit.models.AuditLog;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface IAuditLogService {

    AuditLog create(AuditLogDTO auditLogDTO);

    Optional<AuditLog> findById(UUID id);

    Page<AuditLog> findAll(int page, int size);
}
