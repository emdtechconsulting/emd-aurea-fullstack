package com.emdtech.aurea.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductPriceRequest(

        @NotNull
        @Positive
        Integer quantity,

        @NotNull
        @Positive
        BigDecimal price

) {
}