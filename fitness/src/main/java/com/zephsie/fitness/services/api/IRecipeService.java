package com.zephsie.fitness.services.api;

import com.zephsie.fitness.dtos.RecipeDTO;
import com.zephsie.fitness.models.entity.Recipe;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface IRecipeService {
    Recipe create(RecipeDTO recipeDTO, UUID userId);

    Optional<Recipe> read(UUID id);

    Page<Recipe> read(int page, int size);

    Page<Recipe> read(int page, int size, UUID userId);
}
