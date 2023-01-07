package com.zephsie.report.feign;

import com.zephsie.report.dtos.AuditLogDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "audit-server")
public interface AuditService {

    @PostMapping(value = "/system/audit", consumes = "application/json", produces = "application/json")
    void create(@RequestBody AuditLogDTO auditLogDTO);
}