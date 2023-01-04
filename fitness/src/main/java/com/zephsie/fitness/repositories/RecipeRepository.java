package com.zephsie.fitness.repositories;

import com.zephsie.fitness.models.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, UUID> {
    @NonNull
    @EntityGraph(value = "recipeWithCompositionsAndProducts", type = EntityGraph.EntityGraphType.LOAD)
    Page<Recipe> findAll(@NonNull Pageable pageable);

    @EntityGraph(value = "recipeWithCompositionsAndProducts", type = EntityGraph.EntityGraphType.LOAD)
    Page<Recipe> findAllByUserId(Pageable pageable, UUID userId);

    @NonNull
    @EntityGraph(value = "recipeWithCompositionsAndProducts", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Recipe> findById(@NonNull UUID id);
}