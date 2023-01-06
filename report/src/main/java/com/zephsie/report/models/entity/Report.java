package com.zephsie.report.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zephsie.report.models.api.IBaseEntity;
import com.zephsie.report.utils.serializers.CustomLocalDateTimeDesSerializer;
import com.zephsie.report.utils.serializers.CustomLocalDateTimeSerializer;
import com.zephsie.report.utils.views.EntityView;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "report", schema = "structure")
@DynamicUpdate
@NoArgsConstructor
@JsonView(EntityView.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Report implements IBaseEntity<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @JsonView(EntityView.Base.class)
    private UUID id;

    @Column(name = "report_type", nullable = false)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @JsonView(EntityView.Base.class)
    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    @Column(name = "status", nullable = false)
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @JsonView(EntityView.Base.class)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "user_id")
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    @JsonView(EntityView.Full.class)
    private UUID userId;

    @Column(name = "dt_from", columnDefinition = "TIMESTAMP", precision = 3)
    @Access(AccessType.PROPERTY)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDesSerializer.class)
    @Getter
    @Setter
    @JsonView(EntityView.Base.class)
    private LocalDateTime dtFrom;

    @Column(name = "dt_to", columnDefinition = "TIMESTAMP", precision = 3)
    @Access(AccessType.PROPERTY)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDesSerializer.class)
    @Getter
    @Setter
    @JsonView(EntityView.Base.class)
    private LocalDateTime dtTo;

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