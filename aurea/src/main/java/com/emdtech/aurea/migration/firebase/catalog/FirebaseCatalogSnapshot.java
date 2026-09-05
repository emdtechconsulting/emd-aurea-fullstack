package com.emdtech.aurea.migration.firebase.catalog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class FirebaseCatalogSnapshot {

    private FirebaseCatalogSnapshot() {
    }

    public record CatalogEntry(
            String category,
            String name,
            int quantity,
            BigDecimal price) {
    }

    public static List<CatalogEntry> entries() {

        List<CatalogEntry> entries =
                new ArrayList<>();

        // TRIPLES
        add(entries,
                "TRIPLES",
                "Clásico (huevo, palta, tomate)",
                25, 40);
        add(entries,
                "TRIPLES",
                "Clásico (huevo, palta, tomate)",
                50, 70);
        add(entries,
                "TRIPLES",
                "Clásico (huevo, palta, tomate)",
                100, 130);

        add(entries,
                "TRIPLES",
                "Mixto (jamón y queso)",
                25, 35);
        add(entries,
                "TRIPLES",
                "Mixto (jamón y queso)",
                50, 65);
        add(entries,
                "TRIPLES",
                "Mixto (jamón y queso)",
                100, 120);

        add(entries,
                "TRIPLES",
                "Mixto tropical (jamón, queso y durazno)",
                25, 40);
        add(entries,
                "TRIPLES",
                "Mixto tropical (jamón, queso y durazno)",
                50, 70);
        add(entries,
                "TRIPLES",
                "Mixto tropical (jamón, queso y durazno)",
                100, 130);

        add(entries,
                "TRIPLES",
                "Mixto especial (pollo, jamón y queso)",
                25, 45);
        add(entries,
                "TRIPLES",
                "Mixto especial (pollo, jamón y queso)",
                50, 80);
        add(entries,
                "TRIPLES",
                "Mixto especial (pollo, jamón y queso)",
                100, 140);

        add(entries,
                "TRIPLES",
                "Hawaiano (jamón, pollo y durazno)",
                25, 45);
        add(entries,
                "TRIPLES",
                "Hawaiano (jamón, pollo y durazno)",
                50, 80);
        add(entries,
                "TRIPLES",
                "Hawaiano (jamón, pollo y durazno)",
                100, 140);

        // SANGUCHITOS
        add(entries,
                "SANGUCHITOS",
                "De pollo y apio",
                25, 35);
        add(entries,
                "SANGUCHITOS",
                "De pollo y apio",
                50, 65);
        add(entries,
                "SANGUCHITOS",
                "De pollo y apio",
                100, 120);

        add(entries,
                "SANGUCHITOS",
                "De pollo con apio y durazno",
                25, 40);
        add(entries,
                "SANGUCHITOS",
                "De pollo con apio y durazno",
                50, 70);
        add(entries,
                "SANGUCHITOS",
                "De pollo con apio y durazno",
                100, 130);

        add(entries,
                "SANGUCHITOS",
                "De huevo con tocino",
                25, 35);
        add(entries,
                "SANGUCHITOS",
                "De huevo con tocino",
                50, 65);
        add(entries,
                "SANGUCHITOS",
                "De huevo con tocino",
                100, 120);

        add(entries,
                "SANGUCHITOS",
                "De tocino, espinaca y queso crema",
                25, 45);
        add(entries,
                "SANGUCHITOS",
                "De tocino, espinaca y queso crema",
                50, 80);
        add(entries,
                "SANGUCHITOS",
                "De tocino, espinaca y queso crema",
                100, 140);

        // PETIPANES
        add(entries,
                "PETIPANES",
                "De pollo con apio",
                25, 35);
        add(entries,
                "PETIPANES",
                "De pollo con apio",
                50, 65);
        add(entries,
                "PETIPANES",
                "De pollo con apio",
                100, 120);

        add(entries,
                "PETIPANES",
                "De pollo, apio y durazno",
                25, 40);
        add(entries,
                "PETIPANES",
                "De pollo, apio y durazno",
                50, 70);
        add(entries,
                "PETIPANES",
                "De pollo, apio y durazno",
                100, 130);

        add(entries,
                "PETIPANES",
                "De huevo y tocino",
                25, 35);
        add(entries,
                "PETIPANES",
                "De huevo y tocino",
                50, 65);
        add(entries,
                "PETIPANES",
                "De huevo y tocino",
                100, 120);

        // MINI CROISSANT
        add(entries,
                "MINI CROISSANT",
                "De pollo con apio",
                50, 70);
        add(entries,
                "MINI CROISSANT",
                "De pollo con apio",
                100, 130);

        add(entries,
                "MINI CROISSANT",
                "De pollo, apio y durazno",
                50, 80);
        add(entries,
                "MINI CROISSANT",
                "De pollo, apio y durazno",
                100, 140);

        add(entries,
                "MINI CROISSANT",
                "De queso y jamón",
                50, 70);
        add(entries,
                "MINI CROISSANT",
                "De queso y jamón",
                100, 130);

        add(entries,
                "MINI CROISSANT",
                "De huevo y tocino",
                50, 70);
        add(entries,
                "MINI CROISSANT",
                "De huevo y tocino",
                100, 130);

        // PANES VARIOS
        add(entries,
                "PANES VARIOS",
                "Mini butifarras (sarsa criolla, jamón del país y lechuga)",
                50, 65);
        add(entries,
                "PANES VARIOS",
                "Mini butifarras (sarsa criolla, jamón del país y lechuga)",
                100, 120);

        add(entries,
                "PANES VARIOS",
                "Capresse (tomate, queso fresco, albahaca)",
                50, 70);
        add(entries,
                "PANES VARIOS",
                "Capresse (tomate, queso fresco, albahaca)",
                100, 130);

        // OTROS
        add(entries,
                "OTROS",
                "Tequeños (queso paria)",
                25, 30);
        add(entries,
                "OTROS",
                "Tequeños (queso paria)",
                50, 55);
        add(entries,
                "OTROS",
                "Tequeños (queso paria)",
                100, 100);

        add(entries,
                "OTROS",
                "Mini causitas",
                25, 25);
        add(entries,
                "OTROS",
                "Mini causitas",
                50, 45);
        add(entries,
                "OTROS",
                "Mini causitas",
                100, 80);

        add(entries,
                "OTROS",
                "Mini causitas de pollo",
                25, 30);
        add(entries,
                "OTROS",
                "Mini causitas de pollo",
                50, 50);
        add(entries,
                "OTROS",
                "Mini causitas de pollo",
                100, 90);

        return List.copyOf(entries);
    }

    private static void add(
            List<CatalogEntry> entries,
            String category,
            String name,
            int quantity,
            double price) {

        entries.add(
                new CatalogEntry(
                        category,
                        name,
                        quantity,
                        BigDecimal.valueOf(price)
                )
        );
    }
}