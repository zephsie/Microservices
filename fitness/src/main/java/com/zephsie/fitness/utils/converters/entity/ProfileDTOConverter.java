package com.zephsie.fitness.utils.converters.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zephsie.fitness.dtos.ProfileDTO;
import com.zephsie.fitness.models.entity.Profile;
import com.zephsie.fitness.utils.converters.api.IEntityDTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProfileDTOConverter implements IEntityDTOConverter<Profile, ProfileDTO> {
    private final ObjectMapper objectMapper;

    @Autowired
    public ProfileDTOConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Profile convertToEntity(ProfileDTO dto) {
        return objectMapper.convertValue(dto, Profile.class);
    }
}