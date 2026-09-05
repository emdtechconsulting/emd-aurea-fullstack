package com.emdtech.aurea.controller;

import com.emdtech.aurea.dto.ProductRequest;
import com.emdtech.aurea.dto.ProductResponse;
import com.emdtech.aurea.service.ProductService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(
            ProductService productService
    ) {
        this.productService =
                productService;
    }

    @GetMapping
    public List<ProductResponse> findAll() {
        return productService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(
            @Valid
            @RequestBody
            ProductRequest request
    ) {
        return productService.create(request);
    }
}