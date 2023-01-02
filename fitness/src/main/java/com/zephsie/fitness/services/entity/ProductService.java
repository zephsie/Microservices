package com.zephsie.fitness.services.entity;

import com.zephsie.fitness.dtos.ProductDTO;
import com.zephsie.fitness.models.entity.Product;
import com.zephsie.fitness.repositories.ProductRepository;
import com.zephsie.fitness.services.api.IProductService;
import com.zephsie.fitness.utils.converters.api.IEntityDTOConverter;
import com.zephsie.fitness.utils.exceptions.NotFoundException;
import com.zephsie.fitness.utils.exceptions.WrongVersionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService implements IProductService {
    private final ProductRepository productRepository;

    private final IEntityDTOConverter<Product, ProductDTO> productConverter;

    @Autowired
    public ProductService(ProductRepository productRepository, IEntityDTOConverter<Product, ProductDTO> productConverter) {
        this.productRepository = productRepository;
        this.productConverter = productConverter;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> read(UUID id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> read(int page, int size) {
        return productRepository.findAll(Pageable.ofSize(size).withPage(page));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> read(int page, int size, UUID userId) {
        return productRepository.findAllByUserId(Pageable.ofSize(size).withPage(page), userId);
    }

    @Override
    @Transactional
    public Product create(ProductDTO productDTO, UUID userId) {
        Product product = productConverter.convertToEntity(productDTO);
        product.setUserId(userId);

        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product update(UUID id, ProductDTO productDTO, LocalDateTime version, UUID userId) {
        Optional<Product> optionalProduct = productRepository.findByIdAndUserId(id, userId);

        Product existingProduct = optionalProduct.orElseThrow(() -> new NotFoundException("Product with id " + id + " not found"));

        if (!existingProduct.getDtUpdate().equals(version)) {
            throw new WrongVersionException("Product with id " + id + " has been updated");
        }

        existingProduct.setTitle(productDTO.getTitle());
        existingProduct.setWeight(productDTO.getWeight());
        existingProduct.setCalories(productDTO.getCalories());
        existingProduct.setFats(productDTO.getFats());
        existingProduct.setCarbohydrates(productDTO.getCarbohydrates());
        existingProduct.setProteins(productDTO.getProteins());

        return productRepository.save(existingProduct);
    }
}