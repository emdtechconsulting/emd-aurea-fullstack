package com.emdtech.aurea.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ProductRequest(

        @NotNull
        Long categoryId,

        @NotBlank
        String name,

        String description,

        Boolean active,

        @NotEmpty
        List<@Valid ProductPriceRequest> prices

) {
}