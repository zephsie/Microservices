package com.zephsie.audit.repositories;

import com.zephsie.audit.models.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuditLogRepository extends MongoRepository<AuditLog, UUID> {
    @NonNull
    Page<AuditLog> findAll(@NonNull Pageable pageable);
}
