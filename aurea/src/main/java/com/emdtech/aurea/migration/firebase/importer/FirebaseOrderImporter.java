package com.emdtech.aurea.migration.firebase.importer;

import com.emdtech.aurea.migration.firebase.dto.FirebaseOrderDto;
import com.emdtech.aurea.migration.firebase.importer.FirebaseSingleOrderImportService.ImportAction;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FirebaseOrderImporter {

    private final FirebaseSingleOrderImportService
            singleOrderImportService;

    public FirebaseOrderImporter(
            FirebaseSingleOrderImportService
                    singleOrderImportService) {

        this.singleOrderImportService =
                singleOrderImportService;
    }

    public ImportResult importOrders(
            List<FirebaseOrderDto> sourceOrders) {

        ImportResult result =
                new ImportResult();

        for (FirebaseOrderDto source :
                sourceOrders) {

            try {

                ImportAction action =
                        singleOrderImportService
                                .importSingle(
                                        source
                                );

                switch (action) {

                    case INSERTED ->
                            result.inserted++;

                    case UPDATED ->
                            result.updated++;

                    case SKIPPED ->
                            result.skipped++;
                }

            } catch (Exception exception) {

                result.errors++;

                result.messages.add(
                        "ERROR pedido "
                                + source.getNumeroPedido()
                                + ": "
                                + exception.getMessage()
                );
            }
        }

        return result;
    }

    public static class ImportResult {

        private int inserted;
        private int updated;
        private int skipped;
        private int errors;

        private final List<String> messages =
                new ArrayList<>();

        public int getInserted() {
            return inserted;
        }

        public int getUpdated() {
            return updated;
        }

        public int getSkipped() {
            return skipped;
        }

        public int getErrors() {
            return errors;
        }

        public List<String> getMessages() {
            return List.copyOf(messages);
        }
    }
}