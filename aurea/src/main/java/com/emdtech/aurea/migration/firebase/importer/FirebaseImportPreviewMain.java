package com.emdtech.aurea.migration.firebase.importer;

import com.emdtech.aurea.migration.firebase.dto.FirebaseOrderDto;
import com.emdtech.aurea.migration.firebase.model.CanonicalFirebaseOrder;
import com.emdtech.aurea.migration.firebase.normalizer.FirebaseOrderNormalizer;
import tools.jackson.databind.json.JsonMapper;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FirebaseImportPreviewMain {

    private static final String UNKNOWN_CUSTOMER =
            "CLIENTE NO REGISTRADO - FIREBASE";

    public static void main(String[] args)
            throws Exception {

        if (args.length == 0) {

            System.out.println("Uso:");

            System.out.println(
                    "FirebaseImportPreviewMain "
                            + "<ruta-backup.json>"
            );

            return;
        }

        File file =
                new File(args[0]);

        if (!file.exists()) {

            throw new IllegalArgumentException(
                    "No existe el archivo: "
                            + args[0]
            );
        }

        JsonMapper jsonMapper =
                new JsonMapper();

        FirebaseOrderDto[] array =
                jsonMapper.readValue(
                        file,
                        FirebaseOrderDto[].class
                );

        List<FirebaseOrderDto> orders =
                Arrays.asList(array);

        FirebaseOrderNormalizer normalizer =
                new FirebaseOrderNormalizer();

        int inserts = 0;
        int warnings = 0;
        int temporaryCustomers = 0;
        int products = 0;

        System.out.println();

        System.out.println(
                "========================================"
        );

        System.out.println(
                " AUREA - FIREBASE IMPORT PREVIEW"
        );

        System.out.println(
                "========================================"
        );

        for (FirebaseOrderDto source :
                orders) {

            CanonicalFirebaseOrder canonical =
                    normalizer.normalize(source);

            inserts++;

            if (canonical.getCustomerName()
                    == null
                    || canonical.getCustomerName()
                    .isBlank()) {

                temporaryCustomers++;
                warnings++;

                System.out.println(
                        "WARN pedido "
                                + canonical
                                .getLegacyOrderNumber()
                                + " -> cliente temporal: "
                                + UNKNOWN_CUSTOMER
                );
            }

            if (source.getProductos() != null) {

                products +=
                        source.getProductos().size();
            }
        }

        System.out.println();

        System.out.println(
                "Pedidos leidos..............: "
                        + orders.size()
        );

        System.out.println(
                "INSERT previstos............: "
                        + inserts
        );

        System.out.println(
                "UPDATE previstos............: 0"
        );

        System.out.println(
                "SKIP previstos..............: 0"
        );

        System.out.println(
                "Clientes temporales.........: "
                        + temporaryCustomers
        );

        System.out.println(
                "Advertencias................: "
                        + warnings
        );

        System.out.println(
                "Lineas de productos.........: "
                        + products
        );

        System.out.println();

        System.out.println(
                "ESCRITURA EN MARIADB........: NO"
        );

        System.out.println(
                "========================================"
        );
    }
}