package com.emdtech.aurea.migration.firebase.normalizer;

import com.emdtech.aurea.migration.firebase.dto.FirebaseOrderDto;
import com.emdtech.aurea.migration.firebase.model.CanonicalFirebaseOrder;
import com.emdtech.aurea.order.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class FirebaseOrderNormalizer {

    public CanonicalFirebaseOrder normalize(
            FirebaseOrderDto source) {

        if (source == null) {
            throw new IllegalArgumentException(
                    "El pedido Firebase no puede ser null"
            );
        }

        CanonicalFirebaseOrder target =
                new CanonicalFirebaseOrder();

        target.setFirebaseDocumentId(
                clean(source.getId())
        );

        target.setLegacyOrderNumber(
                source.getNumeroPedido()
        );

        target.setCustomerName(
                clean(source.getCliente())
        );

        /*
         * Existen dos generaciones en Firebase:
         *
         * Nueva:  fecha
         * Antigua: fechaEntrega
         *
         * Preferimos fecha cuando tenga valor.
         */
        target.setDeliveryDate(
                normalizeDate(
                        firstNonBlank(
                                source.getFecha(),
                                source.getFechaEntrega()
                        )
                )
        );

        /*
         * Nueva: hora
         * Antigua: horaEntrega
         */
        target.setDeliveryTime(
                normalizeTime(
                        firstNonBlank(
                                source.getHora(),
                                source.getHoraEntrega()
                        )
                )
        );

        target.setAddress(
                clean(source.getDireccion())
        );

        target.setDistrict(
                clean(source.getDistrito())
        );

        BigDecimal deliveryFee =
                normalizeMoney(
                        source.getCostoDelivery()
                );

        target.setDeliveryFee(
                deliveryFee
        );

        /*
         * Para la migración utilizamos primero
         * el indicador explícito de Firebase.
         *
         * Si no existe, utilizamos el costo
         * como respaldo.
         */
        target.setRequiresDelivery(
                normalizeDelivery(
                        source.getDelivery(),
                        deliveryFee
                )
        );

        target.setObservations(
                clean(source.getObservaciones())
        );

        target.setStatus(
                normalizeStatus(
                        source.getEstado()
                )
        );

        /*
         * estadoPago es la descripción más
         * completa encontrada en el backup:
         *
         * Pagado
         * Pendiente
         * Parcial
         */
        target.setLegacyPaymentStatus(
                normalizePaymentStatus(source)
        );

        target.setLegacyPaymentMethod(
                clean(source.getFormaPago())
        );

        target.setLegacyCreatedAt(
                normalizeTimestamp(
                        firstNonBlank(
                                source.getFechaCreacion(),
                                source.getFechaRegistro()
                        )
                )
        );

        target.setLegacyUpdatedAt(
                normalizeTimestamp(
                        source.getUltimaActualizacion()
                )
        );

        target.setProducts(
                source.getProductos()
        );

        return target;
    }

    private String normalizePaymentStatus(
            FirebaseOrderDto source) {

        String estadoPago =
                clean(source.getEstadoPago());

        if (estadoPago != null) {
            return estadoPago;
        }

        Object pagado =
                source.getPagado();

        if (pagado instanceof Boolean value) {
            return value
                    ? "Pagado"
                    : "Pendiente";
        }

        if (pagado != null) {
            return clean(
                    pagado.toString()
            );
        }

        return "Desconocido";
    }

    private boolean normalizeDelivery(
            Object delivery,
            BigDecimal deliveryFee) {

        if (delivery instanceof Boolean value) {
            return value;
        }

        if (delivery != null) {

            String text =
                    delivery.toString()
                            .trim()
                            .toLowerCase();

            if (text.equals("true")
                    || text.equals("si")
                    || text.equals("sí")
                    || text.equals("1")) {

                return true;
            }

            if (text.equals("false")
                    || text.equals("no")
                    || text.equals("0")) {

                return false;
            }
        }

        return deliveryFee.compareTo(
                BigDecimal.ZERO
        ) > 0;
    }

    private String firstNonBlank(
            String preferred,
            String fallback) {

        String first =
                clean(preferred);

        if (first != null) {
            return first;
        }

        return clean(fallback);
    }

    private String clean(
            String value) {

        if (value == null) {
            return null;
        }

        String trimmed =
                value.trim();

        return trimmed.isEmpty()
                ? null
                : trimmed;
    }

    private LocalDate normalizeDate(
            String value) {

        if (value == null) {
            return null;
        }

        String cleaned =
                value.trim();

        DateTimeFormatter[] formats = {
                DateTimeFormatter.ISO_LOCAL_DATE,
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy"
                ),
                DateTimeFormatter.ofPattern(
                        "d/M/yyyy"
                )
        };

        for (DateTimeFormatter formatter : formats) {

            try {
                return LocalDate.parse(
                        cleaned,
                        formatter
                );

            } catch (
                    DateTimeParseException ignored) {
            }
        }

        return null;
    }

    private LocalTime normalizeTime(
            String value) {

        if (value == null) {
            return null;
        }

        String cleaned =
                value.trim()
                        .toUpperCase();

        if (cleaned.isEmpty()) {
            return null;
        }

        DateTimeFormatter[] formats = {
                DateTimeFormatter.ofPattern(
                        "HH:mm"
                ),
                DateTimeFormatter.ofPattern(
                        "HH:mm:ss"
                ),
                DateTimeFormatter.ofPattern(
                        "h:mm a"
                ),
                DateTimeFormatter.ofPattern(
                        "hh:mm a"
                )
        };

        for (DateTimeFormatter formatter : formats) {

            try {
                return LocalTime.parse(
                        cleaned,
                        formatter
                );

            } catch (
                    DateTimeParseException ignored) {
            }
        }

        return null;
    }

    private LocalDateTime normalizeTimestamp(
            String value) {

        String cleaned =
                clean(value);

        if (cleaned == null) {
            return null;
        }

        try {

            return OffsetDateTime
                    .parse(cleaned)
                    .toLocalDateTime();

        } catch (
                DateTimeParseException ignored) {
        }

        try {

            return LocalDateTime.parse(
                    cleaned
            );

        } catch (
                DateTimeParseException ignored) {
        }

        return null;
    }

    private BigDecimal normalizeMoney(
            Object value) {

        if (value == null) {
            return BigDecimal.ZERO;
        }

        if (value instanceof Number number) {

            return new BigDecimal(
                    number.toString()
            );
        }

        String text =
                value.toString()
                        .trim()
                        .replace("S/", "")
                        .replace("s/", "")
                        .replace(",", "")
                        .trim();

        if (text.isEmpty()) {
            return BigDecimal.ZERO;
        }

        try {

            return new BigDecimal(
                    text
            );

        } catch (
                NumberFormatException exception) {

            return BigDecimal.ZERO;
        }
    }

    private OrderStatus normalizeStatus(
            String legacyStatus) {

        String status =
                clean(legacyStatus);

        if (status == null) {
            return OrderStatus.DRAFT;
        }

        return switch (
                status.toUpperCase()) {

            case "ENTREGADO",
                 "DELIVERED" ->
                    OrderStatus.DELIVERED;

            case "CONFIRMADO",
                 "CONFIRMED" ->
                    OrderStatus.CONFIRMED;

            case "PREPARANDO",
                 "EN PREPARACION",
                 "EN PREPARACIÓN",
                 "PREPARING" ->
                    OrderStatus.PREPARING;

            case "CANCELADO",
                 "CANCELLED",
                 "CANCELED" ->
                    OrderStatus.CANCELLED;

            default ->
                    OrderStatus.DRAFT;
        };
    }
}