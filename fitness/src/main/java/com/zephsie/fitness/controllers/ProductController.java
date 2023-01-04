package com.zephsie.fitness.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.zephsie.fitness.dtos.ProductDTO;
import com.zephsie.fitness.models.entity.Product;
import com.zephsie.fitness.services.api.IProductService;
import com.zephsie.fitness.utils.converters.FieldErrorsToMapConverter;
import com.zephsie.fitness.utils.converters.UnixTimeToLocalDateTimeConverter;
import com.zephsie.fitness.utils.exceptions.BasicFieldValidationException;
import com.zephsie.fitness.utils.exceptions.IllegalParamValuesException;
import com.zephsie.fitness.utils.exceptions.NotFoundException;
import com.zephsie.fitness.utils.views.EntityView;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final IProductService productService;

    private final UnixTimeToLocalDateTimeConverter unixTimeToLocalDateTimeConverter;

    private final FieldErrorsToMapConverter fieldErrorsToMapConverter;

    @Autowired
    public ProductController(IProductService productService,
                             UnixTimeToLocalDateTimeConverter unixTimeToLocalDateTimeConverter,
                             FieldErrorsToMapConverter fieldErrorsToMapConverter) {

        this.productService = productService;
        this.unixTimeToLocalDateTimeConverter = unixTimeToLocalDateTimeConverter;
        this.fieldErrorsToMapConverter = fieldErrorsToMapConverter;
    }

    @JsonView(EntityView.System.class)
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Product> read(@PathVariable("id") UUID id) {

        return productService.read(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Product with id " + id + " not found"));
    }

    @JsonView(EntityView.System.class)
    @GetMapping(produces = "application/json")
    public ResponseEntity<Page<Product>> read(@RequestParam(value = "page", defaultValue = "0") int page,
                                              @RequestParam(value = "size", defaultValue = "10") int size) {

        if (page < 0 || size <= 0) {
            throw new IllegalParamValuesException("Pagination values are not correct");
        }

        return ResponseEntity.ok(productService.read(page, size));
    }

    @JsonView(EntityView.System.class)
    @GetMapping(value = "/my", produces = "application/json")
    public ResponseEntity<Page<Product>> read(@RequestParam(value = "page", defaultValue = "0") int page,
                                              @RequestParam(value = "size", defaultValue = "10") int size,
                                              @RequestHeader("USER_ID") UUID userId) {

        if (page < 0 || size <= 0) {
            throw new IllegalParamValuesException("Pagination values are not correct");
        }

        return ResponseEntity.ok(productService.read(page, size, userId));
    }

    @JsonView(EntityView.System.class)
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Product> create(@RequestBody @Valid ProductDTO productDTO,
                                          BindingResult bindingResult,
                                          @RequestHeader("USER_ID") UUID userId) {

        if (bindingResult.hasErrors()) {
            throw new BasicFieldValidationException(fieldErrorsToMapConverter.map(bindingResult));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(productDTO, userId));
    }

    @JsonView(EntityView.System.class)
    @PutMapping(value = "/{id}/version/{version}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Product> update(@PathVariable("id") UUID id,
                                          @PathVariable("version") long version,
                                          @RequestBody @Valid ProductDTO productDTO,
                                          BindingResult bindingResult,
                                          @RequestHeader("USER_ID") UUID userId) {

        if (bindingResult.hasErrors()) {
            throw new BasicFieldValidationException(fieldErrorsToMapConverter.map(bindingResult));
        }

        return ResponseEntity.ok(productService.update(id, productDTO,
                unixTimeToLocalDateTimeConverter.convert(version), userId));
    }
}