package com.emdtech.aurea.migration.firebase.importer;

import com.emdtech.aurea.migration.firebase.dto.FirebaseOrderDto;
import com.emdtech.aurea.migration.firebase.importer.FirebaseCatalogResolver.CatalogResolution;
import com.emdtech.aurea.migration.firebase.model.CanonicalFirebaseOrder;
import com.emdtech.aurea.migration.firebase.normalizer.FirebaseOrderNormalizer;
import com.emdtech.aurea.order.ItemType;
import com.emdtech.aurea.order.Order;
import com.emdtech.aurea.order.OrderItem;
import com.emdtech.aurea.order.OrderSource;
import com.emdtech.aurea.order.PriceType;
import com.emdtech.aurea.repository.OrderItemRepository;
import com.emdtech.aurea.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Service
public class FirebaseSingleOrderImportService {

    private static final String UNKNOWN_CUSTOMER =
            "CLIENTE NO REGISTRADO - FIREBASE";

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final FirebaseCatalogResolver catalogResolver;

    private final FirebaseOrderNormalizer normalizer =
            new FirebaseOrderNormalizer();

    public FirebaseSingleOrderImportService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            FirebaseCatalogResolver catalogResolver) {

        this.orderRepository =
                orderRepository;

        this.orderItemRepository =
                orderItemRepository;

        this.catalogResolver =
                catalogResolver;
    }

    @Transactional(
            propagation = Propagation.REQUIRES_NEW
    )
    public ImportAction importSingle(
            FirebaseOrderDto source) {

        CanonicalFirebaseOrder canonical =
                normalizer.normalize(source);

        if (canonical.getFirebaseDocumentId()
                == null) {

            throw new IllegalArgumentException(
                    "Firebase ID obligatorio"
            );
        }

        Optional<Order> existingOptional =
                orderRepository
                        .findByFirebaseDocumentId(
                                canonical
                                        .getFirebaseDocumentId()
                        );

        if (existingOptional.isPresent()
                && isUnchanged(
                        existingOptional.get(),
                        canonical
                )) {

            return ImportAction.SKIPPED;
        }

        boolean updating =
                existingOptional.isPresent();

        Order order =
                existingOptional
                        .orElseGet(Order::new);

        mapOrder(
                order,
                canonical
        );

        order =
                orderRepository.save(order);

        /*
         * Si es UPDATE eliminamos únicamente
         * los items del mismo pedido.
         */
        if (updating) {

            List<OrderItem> oldItems =
                    orderItemRepository
                            .findByOrder_IdOrderByIdAsc(
                                    order.getId()
                            );

            if (!oldItems.isEmpty()) {

                orderItemRepository
                        .deleteAll(oldItems);

                orderItemRepository.flush();
            }
        }

        BigDecimal subtotal =
                importItems(
                        order,
                        source
                );

        order.setProductsSubtotal(
                subtotal
        );

        /*
         * El Dry Run demostró previamente
         * que los 76 totales concilian.
         */
        order.setTotal(
                moneyValue(
                        source.getTotal()
                )
        );

        orderRepository.save(order);

        return updating
                ? ImportAction.UPDATED
                : ImportAction.INSERTED;
    }

    private void mapOrder(
            Order order,
            CanonicalFirebaseOrder canonical) {

        String customer =
                canonical.getCustomerName();

        if (customer == null
                || customer.isBlank()) {

            customer =
                    UNKNOWN_CUSTOMER;
        }

        order.setCustomerName(
                customer
        );

        order.setDeliveryDate(
                canonical.getDeliveryDate()
        );

        order.setDeliveryTime(
                canonical.getDeliveryTime()
        );

        order.setRequiresDelivery(
                canonical.isRequiresDelivery()
        );

        order.setAddress(
                canonical.getAddress()
        );

        order.setDistrict(
                canonical.getDistrict()
        );

        order.setDeliveryFee(
                canonical.getDeliveryFee()
        );

        order.setObservations(
                canonical.getObservations()
        );

        order.setStatus(
                canonical.getStatus()
        );

        order.setSource(
                OrderSource.FIREBASE_MIGRATION
        );

        order.setFirebaseDocumentId(
                canonical.getFirebaseDocumentId()
        );

        order.setLegacyOrderNumber(
                canonical.getLegacyOrderNumber()
        );

        order.setLegacyCreatedAt(
                canonical.getLegacyCreatedAt()
        );

        order.setLegacyUpdatedAt(
                canonical.getLegacyUpdatedAt()
        );

        order.setLegacyPaymentStatus(
                canonical.getLegacyPaymentStatus()
        );

        order.setLegacyPaymentMethod(
                canonical.getLegacyPaymentMethod()
        );
    }

    private BigDecimal importItems(
            Order order,
            FirebaseOrderDto source) {

        BigDecimal subtotal =
                BigDecimal.ZERO;

        List<Map<String, Object>> products =
                source.getProductos();

        if (products == null) {
            return subtotal;
        }

        for (Map<String, Object> product :
                products) {

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

            BigDecimal lineTotal =
                    moneyValue(
                            product.get("precio")
                    );

            if (quantity == null
                    || quantity <= 0) {

                throw new IllegalArgumentException(
                        "Cantidad inválida en pedido "
                                + source.getNumeroPedido()
                );
            }

            OrderItem item =
                    new OrderItem();

            item.setOrder(order);

            item.setDescriptionSnapshot(
                    name != null
                            ? name
                            : "PRODUCTO SIN DESCRIPCION"
            );

            item.setQuantity(
                    quantity
            );

            item.setSubtotal(
                    lineTotal
            );

            if (isManual(category)) {

                mapManualItem(
                        item,
                        product,
                        quantity,
                        lineTotal
                );

            } else {

                Optional<CatalogResolution>
                        resolution =
                        catalogResolver.resolve(
                                category,
                                name,
                                quantity,
                                lineTotal
                        );

                if (resolution.isEmpty()) {

                    throw new IllegalStateException(
                            "Producto no reconocido en pedido "
                                    + source.getNumeroPedido()
                                    + ": "
                                    + name
                    );
                }

                CatalogResolution resolved =
                        resolution.get();

                item.setItemType(
                        ItemType.CATALOG
                );

                item.setProduct(
                        resolved.product()
                );

                item.setProductPrice(
                        resolved.productPrice()
                );

                item.setPriceType(
                        PriceType.PACKAGE
                );

                item.setReferencePrice(
                        resolved
                                .productPrice()
                                .getPrice()
                );

                if (resolved.historicalVariant()) {

                    item.setNotes(
                            "Variante histórica Firebase"
                    );
                }
            }

            orderItemRepository.save(item);

            subtotal =
                    subtotal.add(
                            lineTotal
                    );
        }

        return subtotal;
    }

    private void mapManualItem(
            OrderItem item,
            Map<String, Object> product,
            Integer quantity,
            BigDecimal lineTotal) {

        item.setItemType(
                ItemType.MANUAL
        );

        item.setProduct(null);
        item.setProductPrice(null);

        item.setPriceType(
                PriceType.UNIT
        );

        BigDecimal unitPrice =
                moneyNullable(
                        product.get(
                                "precioUnitario"
                        )
                );

        if (unitPrice == null) {

            unitPrice =
                    lineTotal.divide(
                            BigDecimal.valueOf(
                                    quantity
                            ),
                            6,
                            RoundingMode.HALF_UP
                    );
        }

        item.setReferencePrice(
                unitPrice
        );

        item.setNotes(
                "Migrado desde Firebase"
        );
    }

    private boolean isUnchanged(
        Order existing,
        CanonicalFirebaseOrder canonical) {

    if (existing.getLegacyUpdatedAt() != null
            && canonical.getLegacyUpdatedAt() != null) {

        return sameTimestamp(
                existing.getLegacyUpdatedAt(),
                canonical.getLegacyUpdatedAt()
        );
    }

    if (existing.getLegacyCreatedAt() != null
            && canonical.getLegacyCreatedAt() != null) {

        return sameTimestamp(
                existing.getLegacyCreatedAt(),
                canonical.getLegacyCreatedAt()
        );
    }

    /*
     * Si Firebase no tiene timestamps históricos,
     * el firebase_document_id ya identifica de forma
     * única al pedido. Para este importador offline
     * consideramos que no hay cambios.
     */
    return true;
}

private boolean sameTimestamp(
        LocalDateTime databaseValue,
        LocalDateTime firebaseValue) {

    if (databaseValue == null || firebaseValue == null) {
        return databaseValue == null && firebaseValue == null;
    }

    return databaseValue
            .truncatedTo(ChronoUnit.SECONDS)
            .equals(
                    firebaseValue
                            .truncatedTo(ChronoUnit.SECONDS)
            );
}




    private boolean isManual(
            String category) {

        if (category == null) {
            return false;
        }

        String value =
                category
                        .trim()
                        .toLowerCase();

        return value.equals("manual")
                || value.equals(
                        "producto manual"
                );
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

        BigDecimal result =
                moneyNullable(value);

        return result != null
                ? result
                : BigDecimal.ZERO;
    }

    private BigDecimal moneyNullable(
            Object value) {

        if (value == null) {
            return null;
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

            return null;
        }
    }

    public enum ImportAction {
        INSERTED,
        UPDATED,
        SKIPPED
    }
}