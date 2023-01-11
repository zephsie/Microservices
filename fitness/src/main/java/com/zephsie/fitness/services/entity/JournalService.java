package com.zephsie.fitness.services.entity;

import com.zephsie.fitness.dtos.JournalDTO;
import com.zephsie.fitness.models.entity.*;
import com.zephsie.fitness.repositories.JournalRepository;
import com.zephsie.fitness.repositories.ProductRepository;
import com.zephsie.fitness.repositories.ProfileRepository;
import com.zephsie.fitness.repositories.RecipeRepository;
import com.zephsie.fitness.services.api.IJournalService;
import com.zephsie.fitness.utils.exceptions.AccessDeniedException;
import com.zephsie.fitness.utils.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Service
public class JournalService implements IJournalService {
    private final JournalRepository journalRepository;

    private final ProductRepository productRepository;

    private final RecipeRepository recipeRepository;

    private final ProfileRepository profileRepository;

    @Autowired
    public JournalService(JournalRepository journalRepository, ProductRepository productRepository, RecipeRepository recipeRepository, ProfileRepository profileRepository) {
        this.journalRepository = journalRepository;
        this.productRepository = productRepository;
        this.recipeRepository = recipeRepository;
        this.profileRepository = profileRepository;
    }

    @Override
    @Transactional
    public Journal createWithProduct(JournalDTO journalDTO, UUID userId) {
        Profile profile = profileRepository.findById(userId).orElseThrow(() -> new NotFoundException("Profile not found"));

        Product product = productRepository.findById(journalDTO.getProduct().getId()).orElseThrow(() -> new NotFoundException("Product not found"));

        JournalProduct journalProduct = new JournalProduct();

        journalProduct.setDtSupply(journalDTO.getDtSupply());
        journalProduct.setProduct(product);
        journalProduct.setWeight(journalDTO.getWeight());
        journalProduct.setProfile(profile);

        return journalRepository.save(journalProduct);
    }

    @Override
    @Transactional
    public Journal createWithRecipe(JournalDTO journalDTO, UUID userId) {
        Profile profile = profileRepository.findById(userId).orElseThrow(() -> new NotFoundException("Profile not found"));

        Recipe recipe = recipeRepository.findById(journalDTO.getRecipe().getId()).orElseThrow(() -> new NotFoundException("Recipe not found"));

        JournalRecipe journalRecipe = new JournalRecipe();

        journalRecipe.setDtSupply(journalDTO.getDtSupply());
        journalRecipe.setRecipe(recipe);
        journalRecipe.setWeight(journalDTO.getWeight());
        journalRecipe.setProfile(profile);

        return journalRepository.save(journalRecipe);
    }

    @Override
    @Transactional(readOnly = true)
    public Journal read(UUID id, UUID userId) {
        Journal journal = journalRepository.findById(id).orElseThrow(() -> new NotFoundException("Journal not found"));

        if (journal.getProfile() == null || !journal.getProfile().getId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }

        return journal;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Journal> read(int page, int size, UUID userId) {
        Profile profile = profileRepository.findById(userId).orElseThrow(() -> new NotFoundException("Profile not found"));
        return journalRepository.findAllByProfile(profile, Pageable.ofSize(size).withPage(page));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Journal> read(UUID userId, LocalDateTime dtSupplyStart, LocalDateTime dtSupplyEnd) {
        Profile profile = profileRepository.findById(userId).orElseThrow(() -> new NotFoundException("Profile not found"));
        return journalRepository.findAllByProfileAndDtSupplyBetween(profile, dtSupplyStart, dtSupplyEnd);
    }
}