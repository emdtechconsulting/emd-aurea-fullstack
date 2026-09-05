package com.emdtech.aurea.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ProductResponse(

        Long id,

        Long categoryId,
        String categoryName,

        String name,
        String description,

        Boolean active,

        List<ProductPriceResponse> prices,

        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}