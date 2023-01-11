package com.zephsie.wellbeing.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.zephsie.wellbeing.dtos.AuditLogFullDTO;
import com.zephsie.wellbeing.feign.AuditLogFeignService;
import com.zephsie.wellbeing.services.api.IAuditLogService;
import com.zephsie.wellbeing.utils.exceptions.IllegalParamValuesException;
import com.zephsie.wellbeing.utils.views.EntityView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/audit")
public class AuditLogController {

    private final AuditLogFeignService auditLogFeignService;

    private final IAuditLogService auditService;

    @Autowired
    public AuditLogController(AuditLogFeignService auditLogFeignService, IAuditLogService auditService) {
        this.auditLogFeignService = auditLogFeignService;
        this.auditService = auditService;
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @JsonView(EntityView.System.class)
    public ResponseEntity<AuditLogFullDTO> read(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(auditService.read(auditLogFeignService.read(id)));
    }

    @GetMapping(produces = "application/json")
    @JsonView(EntityView.System.class)
    public ResponseEntity<Page<AuditLogFullDTO>> read(@RequestParam(value = "page", defaultValue = "0") int page,
                                                      @RequestParam(value = "size", defaultValue = "10") int size) {

        if (page < 0 || size <= 0) {
            throw new IllegalParamValuesException("Pagination values are not correct");
        }

        return ResponseEntity.ok(auditService.read(auditLogFeignService.read(page, size)));
    }
}