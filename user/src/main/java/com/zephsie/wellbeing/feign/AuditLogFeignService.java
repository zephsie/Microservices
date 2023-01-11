package com.zephsie.wellbeing.feign;

import com.zephsie.wellbeing.dtos.AuditLogDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "audit-server")
public interface AuditLogFeignService {

    @GetMapping(value = "/api/audit/{id}", produces = "application/json")
    AuditLogDTO read(@PathVariable("id") UUID id);

    @GetMapping(value = "/api/audit", produces = "application/json")
    Page<AuditLogDTO> read(@RequestParam(value = "page", defaultValue = "0") int page,
                           @RequestParam(value = "size", defaultValue = "10") int size);
}