package com.emdtech.aurea.migration.firebase.model;

import com.emdtech.aurea.order.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class CanonicalFirebaseOrder {

    private String firebaseDocumentId;

    private Integer legacyOrderNumber;

    private String customerName;

    private LocalDate deliveryDate;

    private LocalTime deliveryTime;

    private String address;

    private String district;

    private boolean requiresDelivery;

    private BigDecimal deliveryFee =
            BigDecimal.ZERO;

    private String observations;

    private OrderStatus status;

    private String legacyPaymentStatus;

    private String legacyPaymentMethod;

    private LocalDateTime legacyCreatedAt;

    private LocalDateTime legacyUpdatedAt;

    private List<Map<String, Object>> products;

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

    public boolean isRequiresDelivery() {
        return requiresDelivery;
    }

    public void setRequiresDelivery(
            boolean requiresDelivery) {

        this.requiresDelivery =
                requiresDelivery;
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

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(
            OrderStatus status) {

        this.status =
                status;
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

    public List<Map<String, Object>> getProducts() {
        return products;
    }

    public void setProducts(
            List<Map<String, Object>> products) {

        this.products =
                products;
    }
}