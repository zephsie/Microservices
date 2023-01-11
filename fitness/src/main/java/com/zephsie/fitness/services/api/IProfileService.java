package com.zephsie.fitness.services.api;

import com.zephsie.fitness.dtos.ProfileDTO;
import com.zephsie.fitness.models.entity.Profile;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface IProfileService {

    Optional<Profile> read(UUID id);

    Profile create(UUID id, ProfileDTO profileDTO);

    Profile update(UUID id, ProfileDTO profileDTO, LocalDateTime version);

    void delete(UUID id, LocalDateTime version);
}