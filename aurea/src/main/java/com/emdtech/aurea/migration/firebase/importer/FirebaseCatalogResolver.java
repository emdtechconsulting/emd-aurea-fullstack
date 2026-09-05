package com.emdtech.aurea.migration.firebase.importer;

import com.emdtech.aurea.entity.Category;
import com.emdtech.aurea.entity.Product;
import com.emdtech.aurea.entity.ProductPrice;
import com.emdtech.aurea.repository.CategoryRepository;
import com.emdtech.aurea.repository.ProductPriceRepository;
import com.emdtech.aurea.repository.ProductRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Component
public class FirebaseCatalogResolver {

    private static final BigDecimal MONEY_TOLERANCE =
            new BigDecimal("0.01");

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductPriceRepository productPriceRepository;

    public FirebaseCatalogResolver(
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            ProductPriceRepository productPriceRepository) {

        this.categoryRepository =
                categoryRepository;

        this.productRepository =
                productRepository;

        this.productPriceRepository =
                productPriceRepository;
    }

    public Optional<CatalogResolution> resolve(
            String categoryName,
            String historicalName,
            Integer quantity,
            BigDecimal historicalPrice) {

        if (categoryName == null
                || historicalName == null
                || quantity == null
                || historicalPrice == null) {

            return Optional.empty();
        }

        Optional<Category> categoryOptional =
                categoryRepository
                        .findByNameIgnoreCase(
                                categoryName.trim()
                        );

        if (categoryOptional.isEmpty()) {
            return Optional.empty();
        }

        Category category =
                categoryOptional.get();

        List<Product> products =
                productRepository
                        .findByCategory_Id(
                                category.getId()
                        );

        /*
         * Primero buscamos coincidencia exacta
         * nombre + cantidad + precio.
         */
        for (Product product : products) {

            if (!normalize(product.getName())
                    .equals(
                            normalize(
                                    historicalName
                            )
                    )) {

                continue;
            }

            Optional<ProductPrice> price =
                    findPrice(
                            product,
                            quantity,
                            historicalPrice
                    );

            if (price.isPresent()) {

                return Optional.of(
                        new CatalogResolution(
                                product,
                                price.get(),
                                false
                        )
                );
            }
        }

        /*
         * Segunda estrategia:
         * categoría + cantidad + precio.
         *
         * Solo aceptamos el resultado si existe
         * UN único candidato.
         *
         * Esto permite resolver las variantes
         * históricas como "butifarrtias".
         */
        List<CatalogResolution> candidates =
                new ArrayList<>();

        for (Product product : products) {

            Optional<ProductPrice> price =
                    findPrice(
                            product,
                            quantity,
                            historicalPrice
                    );

            price.ifPresent(
                    productPrice ->
                            candidates.add(
                                    new CatalogResolution(
                                            product,
                                            productPrice,
                                            true
                                    )
                            )
            );
        }

        if (candidates.size() == 1) {

            return Optional.of(
                    candidates.get(0)
            );
        }

        return Optional.empty();
    }

    private Optional<ProductPrice> findPrice(
            Product product,
            Integer quantity,
            BigDecimal historicalPrice) {

        return productPriceRepository
                .findByProduct_IdOrderByQuantityAsc(
                        product.getId()
                )
                .stream()
                .filter(
                        productPrice ->
                                productPrice
                                        .getQuantity()
                                        .equals(
                                                quantity
                                        )
                )
                .filter(
                        productPrice ->
                                sameMoney(
                                        productPrice.getPrice(),
                                        historicalPrice
                                )
                )
                .findFirst();
    }

    private boolean sameMoney(
            BigDecimal first,
            BigDecimal second) {

        return first
                .subtract(second)
                .abs()
                .compareTo(
                        MONEY_TOLERANCE
                ) <= 0;
    }

    private String normalize(
            String value) {

        if (value == null) {
            return "";
        }

        String noAccents =
                Normalizer.normalize(
                        value,
                        Normalizer.Form.NFD
                ).replaceAll(
                        "\\p{M}",
                        ""
                );

        return noAccents
                .toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll(
                        "\\s+",
                        " "
                );
    }

    public record CatalogResolution(
            Product product,
            ProductPrice productPrice,
            boolean historicalVariant) {
    }
}