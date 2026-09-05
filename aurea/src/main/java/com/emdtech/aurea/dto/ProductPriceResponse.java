package com.emdtech.aurea.dto;

import java.math.BigDecimal;

public record ProductPriceResponse(

        Long id,
        Integer quantity,
        BigDecimal price,
        Boolean active

) {
}