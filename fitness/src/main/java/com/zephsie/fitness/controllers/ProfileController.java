package com.zephsie.fitness.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.zephsie.fitness.dtos.ProfileDTO;
import com.zephsie.fitness.logging.Logging;
import com.zephsie.fitness.models.entity.Profile;
import com.zephsie.fitness.services.api.IProfileService;
import com.zephsie.fitness.utils.converters.FieldErrorsToMapConverter;
import com.zephsie.fitness.utils.converters.UnixTimeToLocalDateTimeConverter;
import com.zephsie.fitness.utils.exceptions.BasicFieldValidationException;
import com.zephsie.fitness.utils.exceptions.NotFoundException;
import com.zephsie.fitness.utils.views.EntityView;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final IProfileService profileService;

    private final UnixTimeToLocalDateTimeConverter unixTimeToLocalDateTimeConverter;

    private final FieldErrorsToMapConverter fieldErrorsToMapConverter;

    @Autowired
    public ProfileController(IProfileService profileService, UnixTimeToLocalDateTimeConverter unixTimeToLocalDateTimeConverter, FieldErrorsToMapConverter fieldErrorsToMapConverter) {
        this.profileService = profileService;
        this.unixTimeToLocalDateTimeConverter = unixTimeToLocalDateTimeConverter;
        this.fieldErrorsToMapConverter = fieldErrorsToMapConverter;
    }

    @GetMapping(value = "/me", produces = "application/json")
    @JsonView(EntityView.System.class)
    public ResponseEntity<Profile> read(@RequestHeader("USER_ID") UUID userId) {
        return profileService.read(userId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Profile not found"));
    }

    @PostMapping(value = "/me", consumes = "application/json", produces = "application/json")
    @Logging(type = "PROFILE", description = "Create profile", userIdPosition = 2)
    @JsonView(EntityView.System.class)
    public ResponseEntity<Profile> create(@RequestBody @Valid ProfileDTO profileDTO,
                                          BindingResult bindingResult,
                                          @RequestHeader("USER_ID") UUID userId) {

        if (bindingResult.hasErrors()) {
            throw new BasicFieldValidationException(fieldErrorsToMapConverter.map(bindingResult));
        }

        return ResponseEntity.ok(profileService.create(profileDTO, userId));
    }

    @PutMapping(value = "/me/version/{version}", consumes = "application/json", produces = "application/json")
    @Logging(type = "PROFILE", description = "Update profile", userIdPosition = 3)
    @JsonView(EntityView.System.class)
    public ResponseEntity<Profile> update(@PathVariable("version") long version,
                                          @RequestBody @Valid ProfileDTO profileDTO,
                                          BindingResult bindingResult,
                                          @RequestHeader("USER_ID") UUID userId) {

        if (bindingResult.hasErrors()) {
            throw new BasicFieldValidationException(fieldErrorsToMapConverter.map(bindingResult));
        }

        return ResponseEntity.ok(profileService.update(userId, profileDTO, unixTimeToLocalDateTimeConverter.convert(version)));
    }

    @DeleteMapping(value = "/me/version/{version}", produces = "application/json")
    @Logging(type = "PROFILE", description = "Delete profile", userIdPosition = 1)
    @JsonView(EntityView.System.class)
    public ResponseEntity<Void> delete(@PathVariable("version") long version,
                                       @RequestHeader("USER_ID") UUID userId) {

        profileService.delete(userId, unixTimeToLocalDateTimeConverter.convert(version));

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}