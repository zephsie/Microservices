package com.zephsie.fitness.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.zephsie.fitness.dtos.JournalDTO;
import com.zephsie.fitness.logging.Logging;
import com.zephsie.fitness.models.entity.Journal;
import com.zephsie.fitness.services.api.IJournalService;
import com.zephsie.fitness.utils.converters.UnixTimeToLocalDateTimeConverter;
import com.zephsie.fitness.utils.exceptions.BasicFieldValidationException;
import com.zephsie.fitness.utils.exceptions.IllegalParamValuesException;
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/journal")
public class JournalController {

    private final IJournalService journalService;

    private final Validator validator;

    private final UnixTimeToLocalDateTimeConverter unixTimeToLocalDateTimeConverter;

    @Autowired
    public JournalController(IJournalService journalService, Validator validator, UnixTimeToLocalDateTimeConverter unixTimeToLocalDateTimeConverter) {
        this.journalService = journalService;
        this.validator = validator;
        this.unixTimeToLocalDateTimeConverter = unixTimeToLocalDateTimeConverter;
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @JsonView(EntityView.WithMappings.class)
    public ResponseEntity<Journal> read(@PathVariable("id") UUID id,
                                        @RequestHeader("USER_ID") UUID userId) {

        return ResponseEntity.ok(journalService.read(id, userId));
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @JsonView(EntityView.WithMappings.class)
    @Logging(type = "JOURNAL", description = "Create journal", userIdPosition = 1)
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
            throw new IllegalParamValuesException("Pagination values are not correct");
        }

        return ResponseEntity.ok(journalService.read(page, size, userId));
    }

    @GetMapping(value = "/between", produces = "application/json")
    @JsonView(EntityView.WithMappings.class)
    public ResponseEntity<Collection<Journal>> read(@RequestParam(value = "dt_supply_start") long dtSupplyStart,
                                                    @RequestParam(value = "dt_supply_end") long dtSupplyEnd,
                                                    @RequestHeader("USER_ID") UUID userId) {

        if (dtSupplyStart > dtSupplyEnd) {
            throw new IllegalParamValuesException("Start date is greater than end date");
        }

        return ResponseEntity.ok(journalService.read(userId, unixTimeToLocalDateTimeConverter.convert(dtSupplyStart), unixTimeToLocalDateTimeConverter.convert(dtSupplyEnd)));
    }
}