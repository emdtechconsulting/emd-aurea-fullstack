package com.emdtech.aurea.service;

import com.emdtech.aurea.dto.CategoryResponse;
import com.emdtech.aurea.entity.Category;
import com.emdtech.aurea.repository.CategoryRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(
            CategoryRepository categoryRepository
    ) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAllActive() {

        Comparator<Category> categoryComparator =
                Comparator.comparing(
                        Category::getDisplayOrder,
                        Comparator.nullsLast(
                                Integer::compareTo
                        )
                ).thenComparing(
                        Category::getName,
                        String.CASE_INSENSITIVE_ORDER
                );

        return categoryRepository
                .findAll()
                .stream()
                .filter(category ->
                        Boolean.TRUE.equals(
                                category.getActive()
                        )
                )
                .sorted(categoryComparator)
                .map(this::toResponse)
                .toList();
    }

    private CategoryResponse toResponse(
            Category category
    ) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getActive(),
                category.getDisplayOrder()
        );
    }
}
