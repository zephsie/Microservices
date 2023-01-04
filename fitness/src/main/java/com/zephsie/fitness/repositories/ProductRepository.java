package com.zephsie.fitness.repositories;

import com.zephsie.fitness.models.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    @NonNull
    Page<Product> findAll(@NonNull Pageable pageable);

    Page<Product> findAllByUserId(Pageable pageable, UUID userId);

    @NonNull
    Optional<Product> findById(@NonNull UUID id);
}