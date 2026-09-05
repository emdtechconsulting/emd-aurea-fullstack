package com.emdtech.aurea.migration.firebase.dryrun;

import com.emdtech.aurea.migration.firebase.dto.FirebaseOrderDto;
import com.emdtech.aurea.migration.firebase.model.CanonicalFirebaseOrder;
import com.emdtech.aurea.migration.firebase.normalizer.FirebaseOrderNormalizer;
import com.emdtech.aurea.order.OrderStatus;
import tools.jackson.databind.json.JsonMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class FirebaseBackupDryRun {

    private final JsonMapper jsonMapper;
    private final FirebaseOrderNormalizer normalizer;

    public FirebaseBackupDryRun() {
        this.jsonMapper = new JsonMapper();
        this.normalizer = new FirebaseOrderNormalizer();
    }

    public void execute(String jsonPath) throws Exception {

        File file = new File(jsonPath);

        if (!file.exists()) {
            throw new IllegalArgumentException(
                    "No existe el archivo: " + jsonPath
            );
        }

        FirebaseOrderDto[] sourceOrders =
                jsonMapper.readValue(
                        file,
                        FirebaseOrderDto[].class
                );

        List<FirebaseOrderDto> orders =
                Arrays.asList(sourceOrders);

        /*
         * Conciliación de productos y totales.
         *
         * Importante:
         * este proceso SOLO analiza.
         * No escribe en MariaDB.
         */
        FirebaseProductReconciler reconciler =
                new FirebaseProductReconciler();

        FirebaseProductReconciler.ReconciliationReport
                reconciliation =
                reconciler.reconcile(orders);

        int normalized = 0;
        int withWarnings = 0;
        int missingCustomer = 0;
        int missingDate = 0;
        int missingTime = 0;
        int missingFirebaseId = 0;
        int totalProducts = 0;

        Map<OrderStatus, Integer> statuses =
                new EnumMap<>(OrderStatus.class);

        for (OrderStatus status : OrderStatus.values()) {
            statuses.put(status, 0);
        }

        List<String> warnings =
                new ArrayList<>();

        for (FirebaseOrderDto source : orders) {

            CanonicalFirebaseOrder target;

            try {

                target =
                        normalizer.normalize(source);

                normalized++;

            } catch (Exception exception) {

                withWarnings++;

                warnings.add(
                        "ERROR normalizando pedido "
                                + source.getNumeroPedido()
                                + " / Firebase ID: "
                                + source.getId()
                                + " / "
                                + exception.getMessage()
                );

                continue;
            }

            boolean warning = false;

            List<String> reasons =
                    new ArrayList<>();

            if (target.getFirebaseDocumentId() == null) {

                missingFirebaseId++;
                warning = true;
                reasons.add("sin Firebase ID");
            }

            if (target.getCustomerName() == null) {

                missingCustomer++;
                warning = true;
                reasons.add("sin cliente");
            }

            if (target.getDeliveryDate() == null) {

                missingDate++;
                warning = true;
                reasons.add("sin fecha");
            }

            if (target.getDeliveryTime() == null) {

                missingTime++;
                warning = true;
                reasons.add("sin hora");
            }

            if (target.getProducts() != null) {

                totalProducts +=
                        target.getProducts().size();
            }

            if (target.getStatus() != null) {

                statuses.compute(
                        target.getStatus(),
                        (key, value) ->
                                value == null
                                        ? 1
                                        : value + 1
                );
            }

            if (warning) {

                withWarnings++;

                warnings.add(
                        "Pedido "
                                + target.getLegacyOrderNumber()
                                + " | Firebase ID: "
                                + target.getFirebaseDocumentId()
                                + " | "
                                + String.join(
                                        ", ",
                                        reasons
                                )
                );
            }
        }

        printReport(
                orders.size(),
                normalized,
                withWarnings,
                missingFirebaseId,
                missingCustomer,
                missingDate,
                missingTime,
                totalProducts,
                statuses,
                warnings,
                reconciliation
        );
    }

    private void printReport(
            int totalOrders,
            int normalized,
            int withWarnings,
            int missingFirebaseId,
            int missingCustomer,
            int missingDate,
            int missingTime,
            int totalProducts,
            Map<OrderStatus, Integer> statuses,
            List<String> warnings,
            FirebaseProductReconciler.ReconciliationReport
                    reconciliation) {

        System.out.println();

        System.out.println(
                "========================================"
        );

        System.out.println(
                " AUREA - FIREBASE MIGRATION DRY RUN"
        );

        System.out.println(
                "========================================"
        );

        System.out.println(
                "Pedidos encontrados........: "
                        + totalOrders
        );

        System.out.println(
                "Pedidos normalizados.......: "
                        + normalized
        );

        System.out.println(
                "Pedidos con advertencias...: "
                        + withWarnings
        );

        System.out.println(
                "Sin Firebase ID.............: "
                        + missingFirebaseId
        );

        System.out.println(
                "Sin cliente.................: "
                        + missingCustomer
        );

        System.out.println(
                "Sin fecha entrega...........: "
                        + missingDate
        );

        System.out.println(
                "Sin hora entrega............: "
                        + missingTime
        );

        System.out.println(
                "Lineas de productos.........: "
                        + totalProducts
        );

        System.out.println();

        System.out.println(
                "Estados:"
        );

        for (OrderStatus status :
                OrderStatus.values()) {

            System.out.printf(
                    "  %-12s : %d%n",
                    status,
                    statuses.getOrDefault(
                            status,
                            0
                    )
            );
        }

        if (!warnings.isEmpty()) {

            System.out.println();

            System.out.println(
                    "Advertencias detalladas:"
            );

            for (String warning : warnings) {

                System.out.println(
                        "  - " + warning
                );
            }
        }

        /*
         * =====================================
         * CONCILIACION DE PRODUCTOS
         * =====================================
         */

        System.out.println();

        System.out.println(
                "----------------------------------------"
        );

        System.out.println(
                " CONCILIACION DE PRODUCTOS"
        );

        System.out.println(
                "----------------------------------------"
        );

        System.out.println(
                "Pedidos analizados.........: "
                        + reconciliation.getOrdersAnalyzed()
        );

        System.out.println(
                "Lineas analizadas...........: "
                        + reconciliation.getProductLines()
        );

        System.out.println(
                "Catalogo exacto.............: "
                        + reconciliation.getExactCatalogLines()
        );

        System.out.println(
                "Productos manuales..........: "
                        + reconciliation.getManualLines()
        );

        System.out.println(
                "Variantes detectadas........: "
                        + reconciliation.getVariantLines()
        );

        System.out.println(
                "No reconocidos..............: "
                        + reconciliation.getUnrecognizedLines()
        );

        /*
         * =====================================
         * VARIANTES
         * =====================================
         */

        if (!reconciliation
                .getVariantDetails()
                .isEmpty()) {

            System.out.println();

            System.out.println(
                    "Variantes detectadas:"
            );

            for (String detail :
                    reconciliation
                            .getVariantDetails()) {

                System.out.println(
                        "  - " + detail
                );
            }
        }

        /*
         * =====================================
         * NO RECONOCIDOS
         * =====================================
         */

        if (!reconciliation
                .getUnrecognizedDetails()
                .isEmpty()) {

            System.out.println();

            System.out.println(
                    "Productos no reconocidos:"
            );

            for (String detail :
                    reconciliation
                            .getUnrecognizedDetails()) {

                System.out.println(
                        "  - " + detail
                );
            }
        }

        /*
         * =====================================
         * CONCILIACION DE TOTALES
         * =====================================
         */

        System.out.println();

        System.out.println(
                "----------------------------------------"
        );

        System.out.println(
                " CONCILIACION DE TOTALES"
        );

        System.out.println(
                "----------------------------------------"
        );

        System.out.println(
                "Pedidos conciliados.........: "
                        + reconciliation
                                .getReconciledOrders()
        );

        System.out.println(
                "Pedidos con diferencia......: "
                        + reconciliation
                                .getOrdersWithTotalDifference()
        );

        if (!reconciliation
                .getTotalDifferenceDetails()
                .isEmpty()) {

            System.out.println();

            System.out.println(
                    "Diferencias encontradas:"
            );

            for (String detail :
                    reconciliation
                            .getTotalDifferenceDetails()) {

                System.out.println(
                        "  - " + detail
                );
            }
        }

        System.out.println();

        System.out.println(
                "========================================"
        );

        System.out.println(
                "ESCRITURA EN MARIADB........: NO"
        );

        System.out.println(
                "========================================"
        );
    }
}