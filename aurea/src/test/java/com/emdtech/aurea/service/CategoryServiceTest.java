package com.emdtech.aurea.service;

import com.emdtech.aurea.dto.CategoryResponse;
import com.emdtech.aurea.entity.Category;
import com.emdtech.aurea.repository.CategoryRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void shouldReturnOnlyActiveCategoriesInDisplayOrder() {

        Category otros =
                createCategory(
                        "OTROS",
                        true,
                        null
                );

        Category triples =
                createCategory(
                        "TRIPLES",
                        true,
                        1
                );

        Category inactive =
                createCategory(
                        "INACTIVA",
                        false,
                        0
                );

        when(categoryRepository.findAll())
                .thenReturn(
                        List.of(
                                otros,
                                inactive,
                                triples
                        )
                );

        List<CategoryResponse> result =
                categoryService.findAllActive();

        assertEquals(2, result.size());
        assertEquals("TRIPLES", result.get(0).name());
        assertEquals("OTROS", result.get(1).name());
    }

    @Test
    void shouldReturnEmptyListWhenRepositoryIsEmpty() {

        when(categoryRepository.findAll())
                .thenReturn(List.of());

        List<CategoryResponse> result =
                categoryService.findAllActive();

        assertTrue(result.isEmpty());
    }

    private Category createCategory(
            String name,
            boolean active,
            Integer displayOrder
    ) {
        Category category = new Category();

        category.setName(name);
        category.setActive(active);
        category.setDisplayOrder(displayOrder);

        return category;
    }
}
