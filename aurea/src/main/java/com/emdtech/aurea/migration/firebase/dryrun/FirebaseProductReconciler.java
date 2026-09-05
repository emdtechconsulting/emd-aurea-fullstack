package com.emdtech.aurea.migration.firebase.dryrun;

import com.emdtech.aurea.migration.firebase.catalog.FirebaseCatalogSnapshot;
import com.emdtech.aurea.migration.firebase.catalog.FirebaseCatalogSnapshot.CatalogEntry;
import com.emdtech.aurea.migration.firebase.dto.FirebaseOrderDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FirebaseProductReconciler {

    private static final BigDecimal MONEY_TOLERANCE =
            new BigDecimal("0.01");

    private final List<CatalogEntry> catalog =
            FirebaseCatalogSnapshot.entries();

    public ReconciliationReport reconcile(
            List<FirebaseOrderDto> orders) {

        ReconciliationReport report =
                new ReconciliationReport();

        for (FirebaseOrderDto order : orders) {

            report.ordersAnalyzed++;

            BigDecimal calculatedSubtotal =
                    BigDecimal.ZERO;

            List<Map<String, Object>> products =
                    order.getProductos();

            if (products == null) {
                products = List.of();
            }

            for (Map<String, Object> product : products) {

                report.productLines++;

                String category =
                        stringValue(
                                product.get("categoria")
                        );

                String name =
                        stringValue(
                                product.get("nombre")
                        );

                Integer quantity =
                        integerValue(
                                product.get("cantidad")
                        );

                BigDecimal linePrice =
                        moneyValue(
                                product.get("precio")
                        );

                calculatedSubtotal =
                        calculatedSubtotal.add(
                                linePrice
                        );

                if (isManual(category)) {

                    report.manualLines++;
                    continue;
                }

                if (isExactCatalogMatch(
                        category,
                        name,
                        quantity,
                        linePrice)) {

                    report.exactCatalogLines++;
                    continue;
                }

                CatalogEntry probable =
                        findProbableVariant(
                                category,
                                quantity,
                                linePrice
                        );

                if (probable != null) {

                    report.variantLines++;

                    report.variantDetails.add(
                            "Pedido "
                                    + order.getNumeroPedido()
                                    + " | "
                                    + safe(category)
                                    + " | \""
                                    + safe(name)
                                    + "\""
                                    + " -> posible \""
                                    + probable.name()
                                    + "\""
                                    + " | cantidad="
                                    + quantity
                                    + " | precio="
                                    + linePrice
                    );

                } else {

                    report.unrecognizedLines++;

                    report.unrecognizedDetails.add(
                            "Pedido "
                                    + order.getNumeroPedido()
                                    + " | categoría="
                                    + safe(category)
                                    + " | producto=\""
                                    + safe(name)
                                    + "\""
                                    + " | cantidad="
                                    + quantity
                                    + " | precio="
                                    + linePrice
                    );
                }
            }

            BigDecimal deliveryFee =
                    moneyValue(
                            order.getCostoDelivery()
                    );

            BigDecimal calculatedTotal =
                    calculatedSubtotal.add(
                            deliveryFee
                    );

            BigDecimal originalTotal =
                    moneyValue(
                            order.getTotal()
                    );

            /*
             * Algunos registros antiguos no tenían
             * subtotal explícito. Para conciliar,
             * usamos la suma real de productos.
             */
            BigDecimal difference =
                    calculatedTotal.subtract(
                                    originalTotal
                            )
                            .abs();

            if (difference.compareTo(
                    MONEY_TOLERANCE) <= 0) {

                report.reconciledOrders++;

            } else {

                report.ordersWithTotalDifference++;

                report.totalDifferenceDetails.add(
                        "Pedido "
                                + order.getNumeroPedido()
                                + " | productos="
                                + formatMoney(
                                        calculatedSubtotal
                                )
                                + " | delivery="
                                + formatMoney(
                                        deliveryFee
                                )
                                + " | calculado="
                                + formatMoney(
                                        calculatedTotal
                                )
                                + " | Firebase="
                                + formatMoney(
                                        originalTotal
                                )
                                + " | diferencia="
                                + formatMoney(
                                        difference
                                )
                );
            }
        }

        return report;
    }

    private boolean isExactCatalogMatch(
            String category,
            String name,
            Integer quantity,
            BigDecimal price) {

        if (category == null
                || name == null
                || quantity == null) {

            return false;
        }

        for (CatalogEntry entry : catalog) {

            if (normalize(entry.category())
                    .equals(normalize(category))
                    && normalize(entry.name())
                    .equals(normalize(name))
                    && entry.quantity() == quantity
                    && sameMoney(
                            entry.price(),
                            price)) {

                return true;
            }
        }

        return false;
    }

    private CatalogEntry findProbableVariant(
            String category,
            Integer quantity,
            BigDecimal price) {

        if (category == null
                || quantity == null) {

            return null;
        }

        List<CatalogEntry> candidates =
                new ArrayList<>();

        for (CatalogEntry entry : catalog) {

            if (normalize(entry.category())
                    .equals(normalize(category))
                    && entry.quantity() == quantity
                    && sameMoney(
                            entry.price(),
                            price)) {

                candidates.add(entry);
            }
        }

        /*
         * Solo señalamos una variante cuando
         * existe un único candidato.
         * Así evitamos "adivinar" productos.
         */
        if (candidates.size() == 1) {
            return candidates.get(0);
        }

        return null;
    }

    private boolean isManual(
            String category) {

        if (category == null) {
            return false;
        }

        String normalized =
                normalize(category);

        return normalized.equals("manual")
                || normalized.equals(
                        "producto manual"
                );
    }

    private boolean sameMoney(
            BigDecimal first,
            BigDecimal second) {

        if (first == null
                || second == null) {

            return false;
        }

        return first.subtract(second)
                .abs()
                .compareTo(
                        MONEY_TOLERANCE
                ) <= 0;
    }

    private String stringValue(
            Object value) {

        if (value == null) {
            return null;
        }

        String result =
                value.toString().trim();

        return result.isEmpty()
                ? null
                : result;
    }

    private Integer integerValue(
            Object value) {

        if (value == null) {
            return null;
        }

        if (value instanceof Number number) {
            return number.intValue();
        }

        try {

            return new BigDecimal(
                    value.toString().trim()
            ).intValueExact();

        } catch (Exception exception) {

            return null;
        }
    }

    private BigDecimal moneyValue(
            Object value) {

        if (value == null) {
            return BigDecimal.ZERO;
        }

        if (value instanceof Number number) {

            return new BigDecimal(
                    number.toString()
            );
        }

        try {

            return new BigDecimal(
                    value.toString()
                            .trim()
                            .replace("S/", "")
                            .replace("s/", "")
                            .replace(",", "")
                            .trim()
            );

        } catch (Exception exception) {

            return BigDecimal.ZERO;
        }
    }

    private String normalize(
            String value) {

        if (value == null) {
            return "";
        }

        String noAccents =
                Normalizer.normalize(
                        value,
                        Normalizer.Form.NFD
                ).replaceAll(
                        "\\p{M}",
                        ""
                );

        return noAccents
                .toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll(
                        "\\s+",
                        " "
                );
    }

    private String safe(
            String value) {

        return value == null
                ? "(null)"
                : value;
    }

    private String formatMoney(
            BigDecimal value) {

        return value
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                )
                .toPlainString();
    }

    public static class ReconciliationReport {

        private int ordersAnalyzed;
        private int productLines;

        private int exactCatalogLines;
        private int manualLines;
        private int variantLines;
        private int unrecognizedLines;

        private int reconciledOrders;
        private int ordersWithTotalDifference;

        private final List<String> variantDetails =
                new ArrayList<>();

        private final List<String> unrecognizedDetails =
                new ArrayList<>();

        private final List<String> totalDifferenceDetails =
                new ArrayList<>();

        public int getOrdersAnalyzed() {
            return ordersAnalyzed;
        }

        public int getProductLines() {
            return productLines;
        }

        public int getExactCatalogLines() {
            return exactCatalogLines;
        }

        public int getManualLines() {
            return manualLines;
        }

        public int getVariantLines() {
            return variantLines;
        }

        public int getUnrecognizedLines() {
            return unrecognizedLines;
        }

        public int getReconciledOrders() {
            return reconciledOrders;
        }

        public int getOrdersWithTotalDifference() {
            return ordersWithTotalDifference;
        }

        public List<String> getVariantDetails() {
            return List.copyOf(
                    variantDetails
            );
        }

        public List<String> getUnrecognizedDetails() {
            return List.copyOf(
                    unrecognizedDetails
            );
        }

        public List<String> getTotalDifferenceDetails() {
            return List.copyOf(
                    totalDifferenceDetails
            );
        }
    }
}