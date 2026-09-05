package com.emdtech.aurea.importer;

import com.emdtech.aurea.entity.Category;
import com.emdtech.aurea.entity.Product;
import com.emdtech.aurea.entity.ProductPrice;
import com.emdtech.aurea.repository.CategoryRepository;
import com.emdtech.aurea.repository.ProductPriceRepository;
import com.emdtech.aurea.repository.ProductRepository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

@Component
public class CatalogImporter implements ApplicationRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductPriceRepository productPriceRepository;
    private final JsonMapper jsonMapper;

    @Value("${aurea.catalog.import.enabled:false}")
    private boolean enabled;

    public CatalogImporter(
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            ProductPriceRepository productPriceRepository,
            JsonMapper jsonMapper
    ) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.productPriceRepository = productPriceRepository;
        this.jsonMapper = jsonMapper;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        if (!enabled) {
            System.out.println("AUREA catalog importer: disabled.");
            return;
        }

        ClassPathResource resource = new ClassPathResource("data/catalog.json");
        List<CategoryData> catalog;

        try (InputStream inputStream = resource.getInputStream()) {
            catalog = jsonMapper.readValue(
                    inputStream,
                    new TypeReference<List<CategoryData>>() {}
            );
        }

        int categoriesCreated = 0;
        int productsCreated = 0;
        int productsSkipped = 0;
        int pricesCreated = 0;

        for (CategoryData categoryData : catalog) {
            Category category = categoryRepository
                    .findByNameIgnoreCase(categoryData.category())
                    .orElseGet(() -> {
                        Category newCategory = new Category();
                        newCategory.setName(categoryData.category());
                        newCategory.setDisplayOrder(categoryData.displayOrder());
                        newCategory.setActive(true);
                        return categoryRepository.save(newCategory);
                    });

            // If category existed already, keep its data but ensure it is active.
            if (Boolean.FALSE.equals(category.getActive())) {
                category.setActive(true);
                categoryRepository.save(category);
            }

            for (ProductData productData : categoryData.items()) {
                boolean exists = productRepository
                        .existsByCategory_IdAndNameIgnoreCase(
                                category.getId(),
                                productData.name()
                        );

                if (exists) {
                    productsSkipped++;
                    continue;
                }

                Product product = new Product();
                product.setCategory(category);
                product.setName(productData.name());
                product.setDescription(null);
                product.setActive(true);
                Product savedProduct = productRepository.save(product);
                productsCreated++;

                List<ProductPrice> prices = productData.prices().stream()
                        .map(priceData -> {
                            ProductPrice productPrice = new ProductPrice();
                            productPrice.setProduct(savedProduct);
                            productPrice.setQuantity(priceData.quantity());
                            productPrice.setPrice(priceData.price());
                            productPrice.setActive(true);
                            return productPrice;
                        })
                        .toList();

                productPriceRepository.saveAll(prices);
                pricesCreated += prices.size();
            }
        }

        // Count total categories at the end instead of trying to infer whether each was newly created.
        categoriesCreated = (int) categoryRepository.count();

        System.out.println("========================================");
        System.out.println("AUREA catalog import completed");
        System.out.println("Categories in DB : " + categoriesCreated);
        System.out.println("Products created : " + productsCreated);
        System.out.println("Products skipped : " + productsSkipped);
        System.out.println("Prices created   : " + pricesCreated);
        System.out.println("========================================");
    }

    private record CategoryData(
            String category,
            Integer displayOrder,
            List<ProductData> items
    ) {}

    private record ProductData(
            String name,
            List<PriceData> prices
    ) {}

    private record PriceData(
            Integer quantity,
            BigDecimal price
    ) {}
}
