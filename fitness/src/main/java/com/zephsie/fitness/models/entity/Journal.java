package com.zephsie.fitness.models.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zephsie.fitness.models.api.IImmutableEntity;
import com.zephsie.fitness.utils.serializers.CustomLocalDateTimeDesSerializer;
import com.zephsie.fitness.utils.serializers.CustomLocalDateTimeSerializer;
import com.zephsie.fitness.utils.views.EntityView;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NamedEntityGraph(
        name = "Journal",
        subclassSubgraphs = {
                @NamedSubgraph(
                        name = "JournalWithProduct",
                        type = JournalProduct.class,
                        attributeNodes = @NamedAttributeNode("product")
                ),
                @NamedSubgraph(
                        name = "JournalWithRecipe",
                        type = JournalRecipe.class,
                        attributeNodes = @NamedAttributeNode("recipe")
                )
        }
)
public abstract class Journal implements IImmutableEntity<UUID> {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @JsonView(EntityView.Base.class)
    private UUID id;

    @Column(name = "dt_supply")
    @Access(AccessType.PROPERTY)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDesSerializer.class)
    @Getter
    @Setter
    @JsonView(EntityView.Base.class)
    private LocalDateTime dtSupply;

    @Column(name = "weight", nullable = false)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @JsonView(EntityView.Base.class)
    private Integer weight;

    @ManyToOne(targetEntity = Profile.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @JsonView(EntityView.Full.class)
    private Profile profile;

    @Column(name = "dt_create", columnDefinition = "TIMESTAMP", precision = 3)
    @Access(AccessType.FIELD)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDesSerializer.class)
    @CreationTimestamp
    @Getter
    @JsonView(EntityView.System.class)
    private LocalDateTime dtCreate;
}
