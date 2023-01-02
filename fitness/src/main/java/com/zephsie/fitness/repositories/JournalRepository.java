package com.zephsie.fitness.repositories;

import com.zephsie.fitness.models.entity.Journal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JournalRepository extends JpaRepository<Journal, UUID> {
    @NonNull
    Page<Journal> findAllByUserId(@NonNull Pageable pageable, @NonNull UUID userId);

    @NonNull
    Optional<Journal> findByIdAndUserId(@NonNull UUID id, @NonNull UUID userId);
}