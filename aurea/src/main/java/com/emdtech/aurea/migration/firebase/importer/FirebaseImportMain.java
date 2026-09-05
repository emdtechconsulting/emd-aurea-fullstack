package com.emdtech.aurea.migration.firebase.importer;

import com.emdtech.aurea.AureaApplication;
import com.emdtech.aurea.migration.firebase.dto.FirebaseOrderDto;
import com.emdtech.aurea.migration.firebase.importer.FirebaseOrderImporter.ImportResult;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import tools.jackson.databind.json.JsonMapper;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FirebaseImportMain {

    private static final String ENV_BACKUP_PATH =
            "AUREA_FIREBASE_BACKUP";

    public static void main(String[] args)
            throws Exception {

        String backupPath =
                resolveBackupPath(args);

        if (backupPath == null) {

            System.out.println();
            System.out.println(
                    "No se recibio la ruta del backup."
            );

            System.out.println(
                    "Use argumento o variable de entorno "
                            + ENV_BACKUP_PATH
            );

            return;
        }

        File file =
                new File(backupPath);

        if (!file.exists()) {

            throw new IllegalArgumentException(
                    "No existe el archivo: "
                            + backupPath
            );
        }

        System.out.println();
        System.out.println(
                "Backup encontrado correctamente."
        );

        System.out.println(
                "Archivo: "
                        + file.getName()
        );

        JsonMapper jsonMapper =
                new JsonMapper();

        FirebaseOrderDto[] sourceArray =
                jsonMapper.readValue(
                        file,
                        FirebaseOrderDto[].class
                );

        List<FirebaseOrderDto> sourceOrders =
                Arrays.asList(
                        sourceArray
                );

        System.out.println(
                "Pedidos encontrados en backup: "
                        + sourceOrders.size()
        );

        ConfigurableApplicationContext context =
                new SpringApplicationBuilder(
                        AureaApplication.class
                )
                        .web(
                                WebApplicationType.SERVLET
                        )
                        .properties(
                                "server.port=0",
                                "spring.devtools.restart.enabled=false"
                        )
                        .run();

        try {

            FirebaseOrderImporter importer =
                    context.getBean(
                            FirebaseOrderImporter.class
                    );

            System.out.println();
            System.out.println(
                    "========================================"
            );

            System.out.println(
                    " AUREA - FIREBASE IMPORT"
            );

            System.out.println(
                    "========================================"
            );

            ImportResult result =
                    importer.importOrders(
                            sourceOrders
                    );

            System.out.println(
                    "Pedidos leidos..............: "
                            + sourceOrders.size()
            );

            System.out.println(
                    "INSERT......................: "
                            + result.getInserted()
            );

            System.out.println(
                    "UPDATE......................: "
                            + result.getUpdated()
            );

            System.out.println(
                    "SKIP........................: "
                            + result.getSkipped()
            );

            System.out.println(
                    "ERROR.......................: "
                            + result.getErrors()
            );

            if (!result.getMessages().isEmpty()) {

                System.out.println();
                System.out.println(
                        "Errores detallados:"
                );

                for (String message :
                        result.getMessages()) {

                    System.out.println(
                            "  - " + message
                    );
                }
            }

            System.out.println(
                    "========================================"
            );

        } finally {

            context.close();
        }
    }

    private static String resolveBackupPath(
            String[] args) {

        if (args != null
                && args.length > 0
                && args[0] != null
                && !args[0].isBlank()) {

            return args[0].trim();
        }

        String environmentPath =
                System.getenv(
                        ENV_BACKUP_PATH
                );

        if (environmentPath != null
                && !environmentPath.isBlank()) {

            return environmentPath.trim();
        }

        return null;
    }
}