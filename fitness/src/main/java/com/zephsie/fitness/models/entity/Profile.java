package com.zephsie.fitness.models.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zephsie.fitness.models.api.IBaseEntity;
import com.zephsie.fitness.utils.serializers.CustomLocalDateTimeDesSerializer;
import com.zephsie.fitness.utils.serializers.CustomLocalDateTimeSerializer;
import com.zephsie.fitness.utils.views.EntityView;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "profile", schema = "structure")
@DynamicUpdate
@NoArgsConstructor
@JsonView(EntityView.class)
public class Profile implements IBaseEntity<UUID> {

    @Id
    @Column(name = "id")
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @JsonView(EntityView.Base.class)
    private UUID id;

    @Column(name = "height", nullable = false)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @JsonView(EntityView.Base.class)
    private Double height;

    @Column(name = "weight", nullable = false)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @JsonView(EntityView.Base.class)
    private Double weight;

    @Column(name = "target", nullable = false)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @JsonView(EntityView.Base.class)
    private Double target;

    @Column(name = "dt_birthday", columnDefinition = "DATE", nullable = false)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @JsonView(EntityView.Base.class)
    private LocalDate birthday;

    @Column(name = "status", nullable = false)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @JsonView(EntityView.Base.class)
    @Enumerated(EnumType.STRING)
    private ActivityType activityType;

    @Column(name = "gender", nullable = false)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @JsonView(EntityView.Base.class)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Version
    @Column(name = "dt_update", columnDefinition = "TIMESTAMP", precision = 3)
    @Access(AccessType.FIELD)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDesSerializer.class)
    @Getter
    @JsonView(EntityView.System.class)
    private LocalDateTime dtUpdate;

    @Column(name = "dt_create", columnDefinition = "TIMESTAMP", precision = 3)
    @Access(AccessType.FIELD)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDesSerializer.class)
    @CreationTimestamp
    @Getter
    @JsonView(EntityView.System.class)
    private LocalDateTime dtCreate;
}