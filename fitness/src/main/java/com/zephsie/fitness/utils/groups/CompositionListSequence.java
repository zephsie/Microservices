package com.zephsie.fitness.utils.groups;

import jakarta.validation.GroupSequence;

@GroupSequence({
        CompositionListGroup.FirstOrder.class,
        CompositionListGroup.SecondOrder.class,
        CompositionListGroup.ThirdOrder.class,
        CompositionListGroup.FourthOrder.class,
        CompositionListGroup.FifthOrder.class
})
public interface CompositionListSequence {
}
