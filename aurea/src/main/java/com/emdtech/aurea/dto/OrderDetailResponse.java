package com.emdtech.aurea.dto;

import com.emdtech.aurea.order.OrderSource;
import com.emdtech.aurea.order.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@Schema(
        name = "OrderDetailResponse",
        description = "Detalle completo de un pedido incluyendo sus items"
)
public class OrderDetailResponse {

    @Schema(
            description = "Identificador único del pedido",
            example = "8"
    )
    private Long id;


    @Schema(
            description = "Nombre del cliente",
            example = "María Rodríguez"
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
    private boolean requiresDelivery;


    @Schema(
            description = "Dirección de entrega",
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
    private BigDecimal deliveryFee;


    @Schema(
            description = "Subtotal correspondiente a los productos",
            example = "55.00"
    )
    private BigDecimal productsSubtotal;


    @Schema(
            description = "Monto total del pedido",
            example = "63.00"
    )
    private BigDecimal total;


    @Schema(
            description = "Observaciones generales del pedido",
            example = "Entregar después de las 3:00 p. m."
    )
    private String observations;


    @Schema(
            description = "Estado actual del pedido",
            example = "CONFIRMED",
            allowableValues = {
                    "DRAFT",
                    "CONFIRMED",
                    "PREPARING",
                    "DELIVERED",
                    "CANCELLED"
            }
    )
    private OrderStatus status;


    @Schema(
            description = "Origen del pedido",
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
            description = "Lista de productos o items pertenecientes al pedido"
    )
    private List<OrderItemResponse> items;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(
            String customerName) {

        this.customerName = customerName;
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

        this.address = address;
    }


    public String getDistrict() {
        return district;
    }

    public void setDistrict(
            String district) {

        this.district = district;
    }


    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(
            BigDecimal deliveryFee) {

        this.deliveryFee =
                deliveryFee;
    }


    public BigDecimal getProductsSubtotal() {
        return productsSubtotal;
    }

    public void setProductsSubtotal(
            BigDecimal productsSubtotal) {

        this.productsSubtotal =
                productsSubtotal;
    }


    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(
            BigDecimal total) {

        this.total = total;
    }


    public String getObservations() {
        return observations;
    }

    public void setObservations(
            String observations) {

        this.observations =
                observations;
    }


    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(
            OrderStatus status) {

        this.status = status;
    }


    public OrderSource getSource() {
        return source;
    }

    public void setSource(
            OrderSource source) {

        this.source = source;
    }


    public List<OrderItemResponse> getItems() {
        return items;
    }

    public void setItems(
            List<OrderItemResponse> items) {

        this.items = items;
    }
}