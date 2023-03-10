package com.zephsie.fitness.utils.validations.validators;

import com.zephsie.fitness.dtos.CompositionDTO;
import com.zephsie.fitness.utils.validations.annotations.ValidCompositionProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;

public class ValidCompositionPropertiesValidator implements ConstraintValidator<ValidCompositionProperties, Collection<CompositionDTO>> {

    @Override
    public boolean isValid(Collection<CompositionDTO> collection, ConstraintValidatorContext context) {
        return collection.stream()
                .noneMatch(compositionDTO -> (compositionDTO.getProduct() == null)
                        || (compositionDTO.getWeight() == null)
                        || (compositionDTO.getWeight() <= 0));
    }
}