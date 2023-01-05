package com.zephsie.audit.controllers;

import com.zephsie.audit.dto.AuditLogDTO;
import com.zephsie.audit.models.AuditLog;
import com.zephsie.audit.services.api.IAuditLogService;
import com.zephsie.audit.utils.converters.FieldErrorsToMapConverter;
import com.zephsie.audit.utils.exceptions.BasicFieldValidationException;
import com.zephsie.audit.utils.exceptions.IllegalParamValuesException;
import com.zephsie.audit.utils.exceptions.NotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/audit")
@Slf4j
public class AuditController {
    private final IAuditLogService reportService;

    private final FieldErrorsToMapConverter fieldErrorsToMapConverter;

    @Autowired
    public AuditController(IAuditLogService reportService, FieldErrorsToMapConverter fieldErrorsToMapConverter) {
        this.reportService = reportService;
        this.fieldErrorsToMapConverter = fieldErrorsToMapConverter;
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<AuditLog> read(@PathVariable("id") UUID id) {

        return reportService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Audit log not found"));
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<AuditLog> create(@RequestBody @Valid AuditLogDTO auditLogDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BasicFieldValidationException(fieldErrorsToMapConverter.map(bindingResult));
        }

        return ResponseEntity.ok(reportService.create(auditLogDTO));
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<Page<AuditLog>> read(@RequestParam(value = "page", defaultValue = "0") int page,
                                               @RequestParam(value = "size", defaultValue = "10") int size) {

        if (page < 0 || size <= 0) {
            throw new IllegalParamValuesException("Pagination values are not correct");
        }

        return ResponseEntity.ok(reportService.findAll(page, size));
    }
}