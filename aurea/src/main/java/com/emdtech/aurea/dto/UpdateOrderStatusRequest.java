package com.emdtech.aurea.dto;

import com.emdtech.aurea.order.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;


@Schema(
        name = "UpdateOrderStatusRequest",
        description = "Datos necesarios para cambiar el estado de un pedido"
)
public class UpdateOrderStatusRequest {

    @Schema(
            description = "Nuevo estado que se desea asignar al pedido",
            example = "CONFIRMED",
            allowableValues = {
                    "DRAFT",
                    "CONFIRMED",
                    "PREPARING",
                    "DELIVERED",
                    "CANCELLED"
            }
    )
    @NotNull(
            message = "El estado del pedido es obligatorio"
    )
    private OrderStatus status;


    public OrderStatus getStatus() {
        return status;
    }


    public void setStatus(
            OrderStatus status) {

        this.status =
                status;
    }
}