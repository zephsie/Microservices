package com.zephsie.fitness.services.entity;

import com.zephsie.fitness.dtos.RecipeDTO;
import com.zephsie.fitness.models.entity.Composition;
import com.zephsie.fitness.models.entity.Product;
import com.zephsie.fitness.models.entity.Recipe;
import com.zephsie.fitness.repositories.ProductRepository;
import com.zephsie.fitness.repositories.RecipeRepository;
import com.zephsie.fitness.services.api.IRecipeService;
import com.zephsie.fitness.utils.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
public class RecipeService implements IRecipeService {
    private final RecipeRepository recipeRepository;

    private final ProductRepository productRepository;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository, ProductRepository productRepository) {
        this.recipeRepository = recipeRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Recipe create(RecipeDTO recipeDTO, UUID userId) {
        List<Product> products = recipeDTO
                .getComposition()
                .stream()
                .map(compositionDTO -> productRepository
                        .findById(compositionDTO.getProduct().getId())
                        .orElseThrow(() -> new NotFoundException("Product with id " + compositionDTO.getProduct().getId() + " not found")))
                .toList();

        Recipe recipe = new Recipe();
        recipe.setUserId(userId);
        recipe.setTitle(recipeDTO.getTitle());

        IntStream.range(0, products.size())
                .forEach(i -> {
                    Composition composition = new Composition();
                    composition.setProduct(products.get(i));
                    composition.setWeight(recipeDTO.getComposition().get(i).getWeight());
                    recipe.addComposition(composition);
                });

        return recipeRepository.save(recipe);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Recipe> read(UUID id) {
        return recipeRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Recipe> read(int page, int size) {
        return recipeRepository.findAll(Pageable.ofSize(size).withPage(page));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Recipe> read(int page, int size, UUID userId) {
        return recipeRepository.findAllByUserId(Pageable.ofSize(size).withPage(page), userId);
    }
}