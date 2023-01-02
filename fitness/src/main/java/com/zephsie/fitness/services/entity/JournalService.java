package com.zephsie.fitness.services.entity;

import com.zephsie.fitness.dtos.JournalDTO;
import com.zephsie.fitness.models.entity.*;
import com.zephsie.fitness.repositories.JournalRepository;
import com.zephsie.fitness.repositories.ProductRepository;
import com.zephsie.fitness.repositories.RecipeRepository;
import com.zephsie.fitness.services.api.IJournalService;
import com.zephsie.fitness.utils.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class JournalService implements IJournalService {
    private final JournalRepository journalRepository;

    private final ProductRepository productRepository;

    private final RecipeRepository recipeRepository;

    @Autowired
    public JournalService(JournalRepository journalRepository, ProductRepository productRepository, RecipeRepository recipeRepository) {
        this.journalRepository = journalRepository;
        this.productRepository = productRepository;
        this.recipeRepository = recipeRepository;
    }

    @Override
    public Journal createWithProduct(JournalDTO journalDTO, UUID userId) {
        Optional<Product> optionalProduct = productRepository.findById(journalDTO.getProduct().getId());

        Product product = optionalProduct.orElseThrow(() ->
                new NotFoundException("Product with id " + journalDTO.getProduct().getId() + " not found"));

        JournalProduct journalProduct = new JournalProduct();

        journalProduct.setDtSupply(journalDTO.getDtSupply());
        journalProduct.setProduct(product);
        journalProduct.setUserId(userId);
        journalProduct.setWeight(journalDTO.getWeight());

        return journalRepository.save(journalProduct);
    }

    @Override
    public Journal createWithRecipe(JournalDTO journalDTO, UUID userId) {
        Optional<Recipe> optionalRecipe = recipeRepository.findById(journalDTO.getRecipe().getId());

        Recipe recipe = optionalRecipe.orElseThrow(() ->
                new NotFoundException("Recipe with id " + journalDTO.getRecipe().getId() + " not found"));

        JournalRecipe journalRecipe = new JournalRecipe();

        journalRecipe.setDtSupply(journalDTO.getDtSupply());
        journalRecipe.setRecipe(recipe);
        journalRecipe.setUserId(userId);
        journalRecipe.setWeight(journalDTO.getWeight());

        return journalRepository.save(journalRecipe);
    }

    @Override
    public Optional<Journal> read(UUID id, UUID userId) {
        return journalRepository.findByIdAndUserId(id, userId);
    }

    @Override
    public Page<Journal> read(int page, int size, UUID userId) {
        return journalRepository.findAllByUserId(Pageable.ofSize(size).withPage(page), userId);
    }
}