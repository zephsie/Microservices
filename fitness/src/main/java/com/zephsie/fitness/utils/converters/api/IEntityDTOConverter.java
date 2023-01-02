package com.zephsie.fitness.utils.converters.api;

public interface IEntityDTOConverter<E, D> {
    E convertToEntity(D dto);
}
