package com.zephsie.fitness.repositories;

import com.zephsie.fitness.models.entity.Journal;
import com.zephsie.fitness.models.entity.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JournalRepository extends JpaRepository<Journal, UUID> {
    @EntityGraph(value = "Journal", type = EntityGraph.EntityGraphType.LOAD)
    Page<Journal> findAllByProfile(Profile profile, Pageable pageable);

    @NonNull
    @EntityGraph(value = "Journal", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Journal> findById(@NonNull UUID id);

    @EntityGraph(value = "Journal", type = EntityGraph.EntityGraphType.LOAD)
    Collection<Journal> findAllByProfileAndDtSupplyBetween(Profile profile, LocalDateTime dtSupplyStart, LocalDateTime dtSupplyEnd);

    void deleteAllByProfile(Profile profile);
}