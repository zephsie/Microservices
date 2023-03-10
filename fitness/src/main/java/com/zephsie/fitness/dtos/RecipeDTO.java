package com.zephsie.fitness.dtos;

import com.zephsie.fitness.utils.groups.CompositionListGroup;
import com.zephsie.fitness.utils.groups.RecipeDTOGroup;
import com.zephsie.fitness.utils.validations.annotations.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeDTO {

    @NotBlank(message = "Title is required", groups = RecipeDTOGroup.FirstOrder.class)
    private String title;

    @NotEmptyList(message = "Composition is required", groups = {CompositionListGroup.FirstOrder.class})
    @ListWithoutNulls(message = "Composition must not contain empty elements", groups = {CompositionListGroup.SecondOrder.class})
    @ValidCompositionProperties(message = "Composition contains invalid properties", groups = {CompositionListGroup.ThirdOrder.class})
    @NotNullProductIds(groups = {CompositionListGroup.FourthOrder.class})
    @UniqueProductIds(message = "Composition contains duplicate product Ids", groups = {CompositionListGroup.FifthOrder.class})
    private List<CompositionDTO> composition;
}
