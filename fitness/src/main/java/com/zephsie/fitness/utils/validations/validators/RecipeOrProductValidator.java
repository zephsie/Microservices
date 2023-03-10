package com.zephsie.fitness.utils.validations.validators;

import com.zephsie.fitness.dtos.JournalDTO;
import com.zephsie.fitness.utils.validations.annotations.RecipeOrProduct;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RecipeOrProductValidator implements ConstraintValidator<RecipeOrProduct, JournalDTO> {
    @Override
    public boolean isValid(JournalDTO journalDTO, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("journal")
                .addConstraintViolation();

        if (journalDTO.getRecipe() == null) {
            return journalDTO.getProduct() != null && journalDTO.getProduct().getId() != null;
        }

        if (journalDTO.getProduct() == null) {
            return journalDTO.getRecipe().getId() != null;
        }

        return false;
    }
}