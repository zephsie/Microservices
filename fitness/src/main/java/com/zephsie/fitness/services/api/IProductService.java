package com.zephsie.fitness.services.api;

import com.zephsie.fitness.dtos.ProductDTO;
import com.zephsie.fitness.models.entity.Product;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface IProductService {
    Product create(ProductDTO productDTO, UUID userId);

    Optional<Product> read(UUID id);

    Page<Product> read(int page, int size);

    Page<Product> read(int page, int size, UUID userId);

    Product update(UUID id, ProductDTO productDTO, LocalDateTime version, UUID userId);
}
