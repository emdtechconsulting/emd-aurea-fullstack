package com.emdtech.aurea.dto;

import com.emdtech.aurea.order.ItemType;
import com.emdtech.aurea.order.PriceType;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;


@Schema(
        name = "OrderItemResponse",
        description = "Detalle de un producto o item perteneciente a un pedido"
)
public class OrderItemResponse {

    @Schema(
            description = "Identificador único del item",
            example = "8"
    )
    private Long id;


    @Schema(
            description = "Identificador del pedido al que pertenece el item",
            example = "8"
    )
    private Long orderId;


    @Schema(
            description = "Tipo de item",
            example = "MANUAL",
            allowableValues = {
                    "CATALOG",
                    "MANUAL"
            }
    )
    private ItemType itemType;


    @Schema(
            description = "Descripción almacenada del producto al momento de realizar el pedido",
            example = "Triple clásico"
    )
    private String description;


    @Schema(
            description = "Cantidad solicitada",
            example = "2"
    )
    private Integer quantity;


    @Schema(
            description = "Tipo de precio utilizado",
            example = "UNIT",
            allowableValues = {
                    "PACKAGE",
                    "UNIT"
            }
    )
    private PriceType priceType;


    @Schema(
            description = "Precio de referencia utilizado para calcular el item",
            example = "5.00"
    )
    private BigDecimal referencePrice;


    @Schema(
            description = "Subtotal calculado para el item",
            example = "10.00"
    )
    private BigDecimal subtotal;


    @Schema(
            description = "Identificador del producto de catálogo cuando corresponde",
            example = "3",
            nullable = true
    )
    private Long productId;


    @Schema(
            description = "Identificador del precio de catálogo cuando corresponde",
            example = "7",
            nullable = true
    )
    private Long productPriceId;


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public Long getOrderId() {
        return orderId;
    }


    public void setOrderId(
            Long orderId) {

        this.orderId =
                orderId;
    }


    public ItemType getItemType() {
        return itemType;
    }


    public void setItemType(
            ItemType itemType) {

        this.itemType =
                itemType;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(
            String description) {

        this.description =
                description;
    }


    public Integer getQuantity() {
        return quantity;
    }


    public void setQuantity(
            Integer quantity) {

        this.quantity =
                quantity;
    }


    public PriceType getPriceType() {
        return priceType;
    }


    public void setPriceType(
            PriceType priceType) {

        this.priceType =
                priceType;
    }


    public BigDecimal getReferencePrice() {
        return referencePrice;
    }


    public void setReferencePrice(
            BigDecimal referencePrice) {

        this.referencePrice =
                referencePrice;
    }


    public BigDecimal getSubtotal() {
        return subtotal;
    }


    public void setSubtotal(
            BigDecimal subtotal) {

        this.subtotal =
                subtotal;
    }


    public Long getProductId() {
        return productId;
    }


    public void setProductId(
            Long productId) {

        this.productId =
                productId;
    }


    public Long getProductPriceId() {
        return productPriceId;
    }


    public void setProductPriceId(
            Long productPriceId) {

        this.productPriceId =
                productPriceId;
    }
}