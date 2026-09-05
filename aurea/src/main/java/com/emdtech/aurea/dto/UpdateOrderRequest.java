package com.emdtech.aurea.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;


@Schema(
        name = "UpdateOrderRequest",
        description = "Datos generales que pueden modificarse mientras el pedido se encuentra en estado DRAFT"
)
public class UpdateOrderRequest {

    @Schema(
            description = "Nombre del cliente",
            example = "Carlos Mendoza"
    )
    @NotBlank(
            message = "El nombre del cliente es obligatorio"
    )
    private String customerName;


    @Schema(
            description = "Fecha programada para la entrega",
            example = "2026-08-30"
    )
    private LocalDate deliveryDate;


    @Schema(
            description = "Hora programada para la entrega",
            example = "15:30:00"
    )
    private LocalTime deliveryTime;


    @Schema(
            description = "Indica si el pedido requiere delivery",
            example = "true"
    )
    private Boolean requiresDelivery;


    @Schema(
            description = "Dirección de entrega",
            example = "Av. Guardia Civil 500"
    )
    private String address;


    @Schema(
            description = "Distrito de entrega",
            example = "Chorrillos"
    )
    private String district;


    @Schema(
            description = "Costo del servicio de delivery",
            example = "8.00"
    )
    @PositiveOrZero(
            message = "El costo de delivery no puede ser negativo"
    )
    private BigDecimal deliveryFee;


    @Schema(
            description = "Observaciones generales del pedido",
            example = "Entregar después de las 3:00 p. m."
    )
    private String observations;


    public String getCustomerName() {
        return customerName;
    }


    public void setCustomerName(
            String customerName) {

        this.customerName =
                customerName;
    }


    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }


    public void setDeliveryDate(
            LocalDate deliveryDate) {

        this.deliveryDate =
                deliveryDate;
    }


    public LocalTime getDeliveryTime() {
        return deliveryTime;
    }


    public void setDeliveryTime(
            LocalTime deliveryTime) {

        this.deliveryTime =
                deliveryTime;
    }


    public Boolean getRequiresDelivery() {
        return requiresDelivery;
    }


    public void setRequiresDelivery(
            Boolean requiresDelivery) {

        this.requiresDelivery =
                requiresDelivery;
    }


    public String getAddress() {
        return address;
    }


    public void setAddress(
            String address) {

        this.address =
                address;
    }


    public String getDistrict() {
        return district;
    }


    public void setDistrict(
            String district) {

        this.district =
                district;
    }


    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }


    public void setDeliveryFee(
            BigDecimal deliveryFee) {

        this.deliveryFee =
                deliveryFee;
    }


    public String getObservations() {
        return observations;
    }


    public void setObservations(
            String observations) {

        this.observations =
                observations;
    }
}