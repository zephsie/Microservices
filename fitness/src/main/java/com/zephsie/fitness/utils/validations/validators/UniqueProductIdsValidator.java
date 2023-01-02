package com.zephsie.fitness.utils.validations.validators;

import com.zephsie.fitness.dtos.CompositionDTO;
import com.zephsie.fitness.dtos.MinProductDTO;
import com.zephsie.fitness.utils.validations.annotations.UniqueProductIds;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;

public class UniqueProductIdsValidator implements ConstraintValidator<UniqueProductIds, Collection<CompositionDTO>> {

    @Override
    public boolean isValid(Collection<CompositionDTO> collection, ConstraintValidatorContext context) {
        return collection.stream()
                .map(CompositionDTO::getProduct)
                .map(MinProductDTO::getId)
                .distinct()
                .count() == collection.size();
    }
}