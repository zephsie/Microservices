package com.zephsie.fitness.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.zephsie.fitness.dtos.RecipeDTO;
import com.zephsie.fitness.models.entity.Recipe;
import com.zephsie.fitness.services.api.IRecipeService;
import com.zephsie.fitness.utils.exceptions.BasicFieldValidationException;
import com.zephsie.fitness.utils.exceptions.IllegalPaginationValuesException;
import com.zephsie.fitness.utils.exceptions.NotFoundException;
import com.zephsie.fitness.utils.groups.CompositionListSequence;
import com.zephsie.fitness.utils.groups.RecipeDTOSequence;
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
@RequestMapping("/api/recipes")
public class RecipeController {

    private final IRecipeService recipeService;

    private final Validator validator;

    @Autowired
    public RecipeController(IRecipeService recipeService,
                            Validator validator) {

        this.recipeService = recipeService;
        this.validator = validator;
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @JsonView(EntityView.WithMappings.class)
    public ResponseEntity<Recipe> read(@PathVariable("id") UUID id) {

        return recipeService.read(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Recipe with id " + id + " not found"));
    }

    @GetMapping(produces = "application/json")
    @JsonView(EntityView.WithMappings.class)
    public ResponseEntity<Page<Recipe>> read(@RequestParam(value = "page", defaultValue = "0") int page,
                                             @RequestParam(value = "size", defaultValue = "10") int size) {

        if (page < 0 || size <= 0) {
            throw new IllegalPaginationValuesException("Pagination values are not correct");
        }

        return ResponseEntity.ok(recipeService.read(page, size));
    }

    @GetMapping(value = "/my", produces = "application/json")
    @JsonView(EntityView.WithMappings.class)
    public ResponseEntity<Page<Recipe>> read(@RequestParam(value = "page", defaultValue = "0") int page,
                                             @RequestParam(value = "size", defaultValue = "10") int size,
                                             @RequestParam(value = "userId") UUID userId) {

        if (page < 0 || size <= 0) {
            throw new IllegalPaginationValuesException("Pagination values are not correct");
        }

        return ResponseEntity.ok(recipeService.read(page, size, userId));
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @JsonView(EntityView.WithMappings.class)
    public ResponseEntity<Recipe> create(@RequestBody RecipeDTO recipeDTO,
                                         @RequestHeader("USER_ID") UUID userId) {

        Set<ConstraintViolation<RecipeDTO>> set = validator
                .validate(recipeDTO, CompositionListSequence.class, RecipeDTOSequence.class);

        if (!set.isEmpty()) {
            throw new BasicFieldValidationException(set.stream()
                    .collect(HashMap::new, (m, v) -> m.put(v.getPropertyPath().toString(), v.getMessage()), HashMap::putAll));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(recipeService.create(recipeDTO, userId));
    }
}