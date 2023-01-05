package com.zephsie.audit.services.entity;

import com.zephsie.audit.dto.AuditLogDTO;
import com.zephsie.audit.models.AuditLog;
import com.zephsie.audit.repositories.AuditLogRepository;
import com.zephsie.audit.services.api.IAuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuditLogService implements IAuditLogService {
    private final AuditLogRepository auditLogRepository;

    @Autowired
    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    @Transactional
    public AuditLog create(AuditLogDTO auditLogDTO) {
        AuditLog auditLog = new AuditLog();

        auditLog.setUserId(auditLogDTO.getUserId());
        auditLog.setType(auditLogDTO.getType());
        auditLog.setDescription(auditLogDTO.getDescription());

        return auditLogRepository.save(auditLog);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AuditLog> findById(UUID id) {
        return auditLogRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLog> findAll(int page, int size) {
        return auditLogRepository.findAll(Pageable.ofSize(size).withPage(page));
    }
}