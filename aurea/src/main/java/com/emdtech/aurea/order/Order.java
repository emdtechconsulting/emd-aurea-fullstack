package com.emdtech.aurea.order;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_name", nullable = false, length = 150)
    private String customerName;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @Column(name = "delivery_time")
    private LocalTime deliveryTime;

    @Column(name = "requires_delivery", nullable = false)
    private boolean requiresDelivery = false;

    @Column(length = 250)
    private String address;

    @Column(length = 100)
    private String district;

    @Column(
            name = "delivery_fee",
            precision = 10,
            scale = 2,
            nullable = false
    )
    private BigDecimal deliveryFee = BigDecimal.ZERO;

    @Column(
            name = "products_subtotal",
            precision = 10,
            scale = 2,
            nullable = false
    )
    private BigDecimal productsSubtotal = BigDecimal.ZERO;

    @Column(
            precision = 10,
            scale = 2,
            nullable = false
    )
    private BigDecimal total = BigDecimal.ZERO;

    @Column(length = 1000)
    private String observations;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status = OrderStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderSource source = OrderSource.MANUAL;

    // =========================================================
    // FIREBASE / LEGACY MIGRATION
    // =========================================================

    @Column(
            name = "firebase_document_id",
            unique = true,
            length = 128
    )
    private String firebaseDocumentId;

    @Column(name = "legacy_order_number")
    private Integer legacyOrderNumber;

    @Column(name = "legacy_created_at")
    private LocalDateTime legacyCreatedAt;

    @Column(name = "legacy_updated_at")
    private LocalDateTime legacyUpdatedAt;

    @Column(
            name = "legacy_payment_status",
            length = 30
    )
    private String legacyPaymentStatus;

    @Column(
            name = "legacy_payment_method",
            length = 100
    )
    private String legacyPaymentMethod;

    // =========================================================
    // AUDIT TIMESTAMPS
    // =========================================================

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {

        LocalDateTime now =
                LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {

        updatedAt =
                LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

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

        this.total =
                total;
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

        this.status =
                status;
    }

    public OrderSource getSource() {
        return source;
    }

    public void setSource(
            OrderSource source) {

        this.source =
                source;
    }

    public String getFirebaseDocumentId() {
        return firebaseDocumentId;
    }

    public void setFirebaseDocumentId(
            String firebaseDocumentId) {

        this.firebaseDocumentId =
                firebaseDocumentId;
    }

    public Integer getLegacyOrderNumber() {
        return legacyOrderNumber;
    }

    public void setLegacyOrderNumber(
            Integer legacyOrderNumber) {

        this.legacyOrderNumber =
                legacyOrderNumber;
    }

    public LocalDateTime getLegacyCreatedAt() {
        return legacyCreatedAt;
    }

    public void setLegacyCreatedAt(
            LocalDateTime legacyCreatedAt) {

        this.legacyCreatedAt =
                legacyCreatedAt;
    }

    public LocalDateTime getLegacyUpdatedAt() {
        return legacyUpdatedAt;
    }

    public void setLegacyUpdatedAt(
            LocalDateTime legacyUpdatedAt) {

        this.legacyUpdatedAt =
                legacyUpdatedAt;
    }

    public String getLegacyPaymentStatus() {
        return legacyPaymentStatus;
    }

    public void setLegacyPaymentStatus(
            String legacyPaymentStatus) {

        this.legacyPaymentStatus =
                legacyPaymentStatus;
    }

    public String getLegacyPaymentMethod() {
        return legacyPaymentMethod;
    }

    public void setLegacyPaymentMethod(
            String legacyPaymentMethod) {

        this.legacyPaymentMethod =
                legacyPaymentMethod;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}