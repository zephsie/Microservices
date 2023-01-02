package com.zephsie.fitness.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.zephsie.fitness.dtos.JournalDTO;
import com.zephsie.fitness.models.entity.Journal;
import com.zephsie.fitness.services.api.IJournalService;
import com.zephsie.fitness.utils.exceptions.BasicFieldValidationException;
import com.zephsie.fitness.utils.exceptions.IllegalPaginationValuesException;
import com.zephsie.fitness.utils.exceptions.NotFoundException;
import com.zephsie.fitness.utils.groups.BasicJournalFieldsSequence;
import com.zephsie.fitness.utils.groups.TotalJournalSequence;
import com.zephsie.fitness.utils.views.EntityView;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/journal")
public class JournalController {

    private final IJournalService journalService;

    private final Validator validator;

    @Autowired
    public JournalController(IJournalService journalService, Validator validator) {
        this.journalService = journalService;
        this.validator = validator;
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @JsonView(EntityView.WithMappings.class)
    public ResponseEntity<Journal> read(@PathVariable("id") UUID id,
                                        @RequestHeader("USER_ID") UUID userId) {

        return journalService.read(id, userId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Journal with id " + id + " not found"));
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @JsonView(EntityView.WithMappings.class)
    public ResponseEntity<Journal> create(@RequestBody JournalDTO journalDTO,
                                          @RequestHeader("USER_ID") UUID userId) {

        Set<ConstraintViolation<JournalDTO>> set = validator
                .validate(journalDTO, BasicJournalFieldsSequence.class, TotalJournalSequence.class);

        if (!set.isEmpty()) {
            throw new BasicFieldValidationException(set.stream()
                    .collect(HashMap::new, (m, v) -> m.put(v.getPropertyPath().toString(), v.getMessage()), HashMap::putAll));
        }

        return journalDTO.getRecipe() == null ?
                ResponseEntity.status(HttpStatus.CREATED).body(journalService.createWithProduct(journalDTO, userId)) :
                ResponseEntity.status(HttpStatus.CREATED).body(journalService.createWithRecipe(journalDTO, userId));
    }

    @GetMapping(produces = "application/json")
    @JsonView(EntityView.WithMappings.class)
    public ResponseEntity<Page<Journal>> read(@RequestParam(value = "page", defaultValue = "0") int page,
                                              @RequestParam(value = "size", defaultValue = "10") int size,
                                              @RequestHeader("USER_ID") UUID userId) {

        if (page < 0 || size <= 0) {
            throw new IllegalPaginationValuesException("Pagination values are not correct");
        }

        return ResponseEntity.ok(journalService.read(page, size, userId));
    }
}