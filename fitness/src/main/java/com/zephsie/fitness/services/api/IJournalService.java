package com.zephsie.fitness.services.api;

import com.zephsie.fitness.dtos.JournalDTO;
import com.zephsie.fitness.models.entity.Journal;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface IJournalService {
    Journal createWithProduct(JournalDTO journalDTO, UUID userId);

    Journal createWithRecipe(JournalDTO journalDTO, UUID userId);

    Optional<Journal> read(UUID id, UUID userId);

    Page<Journal> read(int page, int size, UUID userId);
}