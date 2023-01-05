package com.zephsie.fitness.repositories;

import com.zephsie.fitness.models.entity.Journal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JournalRepository extends JpaRepository<Journal, UUID> {

    @EntityGraph(value = "journalWithRecipe", type = EntityGraph.EntityGraphType.LOAD)
    Page<Journal> findAllByUserId(Pageable pageable, UUID userId);

    @EntityGraph(value = "journalWithRecipe", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Journal> findByIdAndUserId(UUID id, UUID userId);

    @EntityGraph(value = "journalWithRecipe", type = EntityGraph.EntityGraphType.LOAD)
    Collection<Journal> findAllByUserIdAndDtSupplyBetween(UUID userId, LocalDateTime dtSupplyStart, LocalDateTime dtSupplyEnd);
}