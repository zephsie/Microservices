package com.zephsie.wellbeing.dtos;

import com.fasterxml.jackson.annotation.JsonView;
import com.zephsie.wellbeing.models.entity.User;
import com.zephsie.wellbeing.utils.views.EntityView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonView(EntityView.class)
public class AuditLogFullDTO {

    @JsonView(EntityView.Base.class)
    private UUID id;

    @JsonView(EntityView.System.class)
    private User user;

    @JsonView(EntityView.Base.class)
    private String type;

    @JsonView(EntityView.Base.class)
    private String description;
}