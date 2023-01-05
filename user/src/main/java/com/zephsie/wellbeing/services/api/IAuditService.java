package com.zephsie.wellbeing.services.api;

import com.zephsie.wellbeing.dtos.AuditLogDTO;
import com.zephsie.wellbeing.dtos.AuditLogFullDTO;
import org.springframework.data.domain.Page;

public interface IAuditService {

    AuditLogFullDTO read(AuditLogDTO auditLogDTO);

    Page<AuditLogFullDTO> read(Page<AuditLogDTO> page);
}