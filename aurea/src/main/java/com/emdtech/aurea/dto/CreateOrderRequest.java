package com.emdtech.aurea.dto;

import com.emdtech.aurea.order.OrderSource;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;


@Schema(
        name = "CreateOrderRequest",
        description = "Datos necesarios para crear un nuevo pedido en AUREA"
)
public class CreateOrderRequest {

    @Schema(
            description = "Nombre del cliente",
            example = "María Rodríguez"
    )
    @NotBlank(
            message = "El nombre del cliente es obligatorio"
    )
    private String customerName;


    @Schema(
            description = "Fecha programada de entrega",
            example = "2026-09-05"
    )
    private LocalDate deliveryDate;


    @Schema(
            description = "Hora programada de entrega",
            example = "20:00"
    )
    private LocalTime deliveryTime;


    @Schema(
            description = "Indica si se debe aplicar un costo adicional de delivery",
            example = "true"
    )
    private boolean requiresDelivery;


    @Schema(
            description = "Dirección donde se entregará el pedido",
            example = "Av. Los Cedros 250"
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
            description = "Origen desde donde fue creado el pedido",
            example = "WEB",
            allowableValues = {
                    "WEB",
                    "MOBILE",
                    "FIREBASE_MIGRATION",
                    "MANUAL"
            }
    )
    private OrderSource source;


    @Schema(
            description = "Observaciones o indicaciones adicionales del pedido",
            example = "Entregar en recepción"
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


    public boolean isRequiresDelivery() {
        return requiresDelivery;
    }

    public void setRequiresDelivery(
            boolean requiresDelivery) {

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


    public OrderSource getSource() {
        return source;
    }

    public void setSource(
            OrderSource source) {

        this.source =
                source;
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
