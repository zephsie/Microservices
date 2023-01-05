package com.zephsie.fitness.models.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.zephsie.fitness.utils.views.EntityView;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "journal_recipe", schema = "structure")
@DynamicUpdate
@NoArgsConstructor
@NamedEntityGraph(
        name = "journalWithRecipe",
        attributeNodes = {
                @NamedAttributeNode("recipe"),
                @NamedAttributeNode(value = "recipe", subgraph = "recipeWithCompositionsAndProducts"),
        },
        subgraphs = @NamedSubgraph
                (
                        name = "recipeWithCompositionsAndProducts",
                        attributeNodes = @NamedAttributeNode("composition")
                )
)
public class JournalRecipe extends Journal {

    @ManyToOne(targetEntity = Recipe.class)
    @JoinColumn(name = "recipe_id")
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @JsonView(EntityView.WithMappings.class)
    private Recipe recipe;
}