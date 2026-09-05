package com.emdtech.aurea.repository;

import com.emdtech.aurea.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory_Id(Long categoryId);

    boolean existsByCategory_IdAndNameIgnoreCase(
            Long categoryId,
            String name
    );
}