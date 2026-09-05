package com.emdtech.aurea.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;


@Schema(
        name = "AddManualItemRequest",
        description = "Datos necesarios para agregar un producto manual a un pedido"
)
public class AddManualItemRequest {

    @Schema(
            description = "Descripción del producto o servicio manual",
            example = "Bandeja especial personalizada"
    )
    @NotBlank(
            message = "La descripción es obligatoria"
    )
    private String description;


    @Schema(
            description = "Cantidad solicitada",
            example = "2"
    )
    @NotNull(
            message = "La cantidad es obligatoria"
    )
    @Positive(
            message = "La cantidad debe ser mayor que cero"
    )
    private Integer quantity;


    @Schema(
            description = "Precio unitario del producto manual",
            example = "15.50"
    )
    @NotNull(
            message = "El precio unitario es obligatorio"
    )
    @PositiveOrZero(
            message = "El precio unitario no puede ser negativo"
    )
    private BigDecimal unitPrice;


    @Schema(
            description = "Observaciones específicas del item",
            example = "Sin mayonesa"
    )
    private String notes;


    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description) {

        this.description = description;
    }


    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(
            Integer quantity) {

        this.quantity = quantity;
    }


    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(
            BigDecimal unitPrice) {

        this.unitPrice = unitPrice;
    }


    public String getNotes() {
        return notes;
    }

    public void setNotes(
            String notes) {

        this.notes = notes;
    }
}