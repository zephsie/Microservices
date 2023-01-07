package com.zephsie.fitness.utils.validations.validators;

import com.zephsie.fitness.dtos.CompositionDTO;
import com.zephsie.fitness.utils.validations.annotations.UniqueProductIds;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UniqueProductIdsValidator implements ConstraintValidator<UniqueProductIds, Collection<CompositionDTO>> {

    @Override
    public boolean isValid(Collection<CompositionDTO> collection, ConstraintValidatorContext context) {
        Set<UUID> set = new HashSet<>();

        for (CompositionDTO compositionDTO : collection) {
            if (set.contains(compositionDTO.getProduct().getId())) {
                return false;
            }
            set.add(compositionDTO.getProduct().getId());
        }

        return true;
    }
}