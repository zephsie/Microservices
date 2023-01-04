package com.zephsie.report.models.entity;

import com.zephsie.report.models.api.IImmutableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "report_content", schema = "structure")
@DynamicUpdate
@NoArgsConstructor
public class ReportContent implements IImmutableEntity<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    private UUID id;

    @Column(name = "content")
    @Access(AccessType.PROPERTY)
    @Getter
    @Setter
    private byte[] content;

    @Column(name = "dt_create", columnDefinition = "TIMESTAMP", precision = 3)
    @Access(AccessType.FIELD)
    @CreationTimestamp
    @Getter
    private LocalDateTime dtCreate;
}
