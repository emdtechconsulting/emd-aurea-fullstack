package com.emdtech.aurea.service;

import com.emdtech.aurea.dto.ProductPriceRequest;
import com.emdtech.aurea.dto.ProductPriceResponse;
import com.emdtech.aurea.dto.ProductRequest;
import com.emdtech.aurea.dto.ProductResponse;
import com.emdtech.aurea.entity.Category;
import com.emdtech.aurea.entity.Product;
import com.emdtech.aurea.entity.ProductPrice;
import com.emdtech.aurea.repository.CategoryRepository;
import com.emdtech.aurea.repository.ProductPriceRepository;
import com.emdtech.aurea.repository.ProductRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductPriceRepository productPriceRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(
            ProductRepository productRepository,
            ProductPriceRepository productPriceRepository,
            CategoryRepository categoryRepository
    ) {
        this.productRepository = productRepository;
        this.productPriceRepository = productPriceRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {

        return productRepository
                .findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {

        Category category = categoryRepository
                .findById(request.categoryId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "La categoría no existe"
                        )
                );

        boolean exists =
                productRepository
                        .existsByCategory_IdAndNameIgnoreCase(
                                request.categoryId(),
                                request.name()
                        );

        if (exists) {
            throw new IllegalArgumentException(
                    "Ya existe un producto con ese nombre en la categoría"
            );
        }

        Product product = new Product();

        product.setCategory(category);
        product.setName(request.name());
        product.setDescription(request.description());

        if (request.active() != null) {
            product.setActive(request.active());
        }

        Product savedProduct =
                productRepository.save(product);

        List<ProductPrice> prices =
                request.prices()
                        .stream()
                        .map(priceRequest ->
                                createPrice(
                                        savedProduct,
                                        priceRequest
                                )
                        )
                        .toList();

        productPriceRepository.saveAll(prices);

        return toResponse(savedProduct);
    }

    private ProductPrice createPrice(
            Product product,
            ProductPriceRequest request
    ) {

        ProductPrice productPrice =
                new ProductPrice();

        productPrice.setProduct(product);
        productPrice.setQuantity(request.quantity());
        productPrice.setPrice(request.price());
        productPrice.setActive(true);

        return productPrice;
    }

    private ProductResponse toResponse(
            Product product
    ) {

        List<ProductPriceResponse> prices =
                productPriceRepository
                        .findByProduct_IdOrderByQuantityAsc(
                                product.getId()
                        )
                        .stream()
                        .map(price ->
                                new ProductPriceResponse(
                                        price.getId(),
                                        price.getQuantity(),
                                        price.getPrice(),
                                        price.getActive()
                                )
                        )
                        .toList();

        return new ProductResponse(
                product.getId(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getName(),
                product.getDescription(),
                product.getActive(),
                prices,
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}