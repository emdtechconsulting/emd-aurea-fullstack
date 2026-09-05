package com.emdtech.aurea.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


@Schema(
        name = "AddCatalogItemRequest",
        description = "Datos necesarios para agregar al pedido un producto registrado en el catálogo"
)
public class AddCatalogItemRequest {

    @Schema(
            description = "Identificador del precio de catálogo seleccionado",
            example = "1"
    )
    @NotNull(
            message = "El precio del producto es obligatorio"
    )
    @Positive(
            message = "El identificador del precio debe ser mayor que cero"
    )
    private Long productPriceId;


    public Long getProductPriceId() {
        return productPriceId;
    }


    public void setProductPriceId(
            Long productPriceId) {

        this.productPriceId =
                productPriceId;
    }
}