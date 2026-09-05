package com.emdtech.aurea.service;

import com.emdtech.aurea.dto.UpdateOrderRequest;
import com.emdtech.aurea.entity.Product;
import com.emdtech.aurea.entity.ProductPrice;
import com.emdtech.aurea.exception.BusinessRuleException;
import com.emdtech.aurea.exception.ResourceNotFoundException;
import com.emdtech.aurea.order.ItemType;
import com.emdtech.aurea.order.Order;
import com.emdtech.aurea.order.OrderItem;
import com.emdtech.aurea.order.OrderSource;
import com.emdtech.aurea.order.OrderStatus;
import com.emdtech.aurea.order.PriceType;
import com.emdtech.aurea.repository.OrderItemRepository;
import com.emdtech.aurea.repository.OrderRepository;
import com.emdtech.aurea.repository.ProductPriceRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductPriceRepository productPriceRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ProductPriceRepository productPriceRepository) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productPriceRepository = productPriceRepository;
    }

    // =========================================================
    // CREAR PEDIDO
    // =========================================================

    public Order crearPedido(
            String customerName,
            boolean requiresDelivery,
            String address,
            String district,
            BigDecimal deliveryFee,
            OrderSource source) {

        return crearPedido(
                customerName,
                requiresDelivery,
                address,
                district,
                deliveryFee,
                source,
                null,
                null,
                null
        );
    }


    public Order crearPedido(
            String customerName,
            boolean requiresDelivery,
            String address,
            String district,
            BigDecimal deliveryFee,
            OrderSource source,
            LocalDate deliveryDate,
            LocalTime deliveryTime,
            String observations) {

        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del cliente es obligatorio"
            );
        }

        if (deliveryFee == null) {
            deliveryFee = BigDecimal.ZERO;
        }

        if (deliveryFee.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "El costo de delivery no puede ser negativo"
            );
        }

        /*
         * REGLA DE NEGOCIO:
         *
         * address y district representan el lugar de entrega
         * y son independientes de si se cobra delivery.
         *
         * requiresDelivery solamente indica si debe aplicarse
         * un costo adicional de delivery.
         *
         * Si no se aplica costo de delivery,
         * deliveryFee debe quedar en cero.
         */
        if (!requiresDelivery) {
            deliveryFee = BigDecimal.ZERO;
        }

        Order order = new Order();

        order.setCustomerName(customerName);

        // Fecha y hora se guardan desde la creación del pedido.
        order.setDeliveryDate(deliveryDate);
        order.setDeliveryTime(deliveryTime);

        order.setRequiresDelivery(requiresDelivery);

        // Dirección y distrito se conservan siempre.
        order.setAddress(address);
        order.setDistrict(district);

        order.setDeliveryFee(deliveryFee);

        order.setProductsSubtotal(
                BigDecimal.ZERO
        );

        order.setTotal(
                deliveryFee
        );

        order.setStatus(
                OrderStatus.DRAFT
        );

        if (source == null) {
            source = OrderSource.MANUAL;
        }

        order.setSource(source);

        // Observaciones también forman parte de los datos iniciales.
        order.setObservations(observations);

        return orderRepository.save(order);
    }

    // =========================================================
    // AGREGAR PRODUCTO DE CATÁLOGO
    // =========================================================

    public OrderItem agregarProductoCatalogo(
            Long orderId,
            Long productPriceId) {

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe el pedido con id: "
                                        + orderId
                        )
                );

        validarPedidoEditable(order);

        ProductPrice productPrice =
                productPriceRepository
                        .findById(productPriceId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No existe el precio de producto con id: "
                                                + productPriceId
                                )
                        );

        Product product =
                productPrice.getProduct();

        OrderItem item =
                new OrderItem();

        item.setOrder(order);

        item.setItemType(
                ItemType.CATALOG
        );

        item.setProduct(product);

        item.setProductPrice(
                productPrice
        );

        item.setDescriptionSnapshot(
                product.getName()
        );

        item.setQuantity(
                productPrice.getQuantity()
        );

        item.setPriceType(
                PriceType.PACKAGE
        );

        item.setReferencePrice(
                productPrice.getPrice()
        );

        item.setSubtotal(
                productPrice.getPrice()
        );

        OrderItem savedItem =
                orderItemRepository.save(item);

        recalcularTotales(order);

        return savedItem;
    }

    // =========================================================
    // OBTENER PEDIDO
    // =========================================================

    public Order obtenerPedido(
            Long orderId) {

        return orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe el pedido con id: "
                                        + orderId
                        )
                );
    }

    // =========================================================
    // OBTENER ITEMS DEL PEDIDO
    // =========================================================

    public List<OrderItem> obtenerItemsPedido(
            Long orderId) {

        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException(
                    "No existe el pedido con id: "
                            + orderId
            );
        }

        return orderItemRepository
                .findByOrder_IdOrderByIdAsc(
                        orderId
                );
    }

    // =========================================================
    // AGREGAR PRODUCTO MANUAL
    // =========================================================

    public OrderItem agregarProductoManual(
            Long orderId,
            String description,
            Integer quantity,
            BigDecimal unitPrice,
            String notes) {

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe el pedido con id: "
                                        + orderId
                        )
                );

        validarPedidoEditable(order);

        if (description == null
                || description.isBlank()) {

            throw new IllegalArgumentException(
                    "La descripción del producto manual es obligatoria"
            );
        }

        if (quantity == null
                || quantity <= 0) {

            throw new BusinessRuleException(
                    "La cantidad debe ser mayor que cero"
            );
        }

        if (unitPrice == null) {

            throw new IllegalArgumentException(
                    "El precio unitario es obligatorio"
            );
        }

        if (unitPrice.compareTo(
                BigDecimal.ZERO
        ) < 0) {

            throw new IllegalArgumentException(
                    "El precio unitario no puede ser negativo"
            );
        }

        BigDecimal subtotal =
                unitPrice.multiply(
                        BigDecimal.valueOf(
                                quantity
                        )
                );

        OrderItem item =
                new OrderItem();

        item.setOrder(order);

        item.setItemType(
                ItemType.MANUAL
        );

        item.setProduct(null);

        item.setProductPrice(null);

        item.setDescriptionSnapshot(
                description
        );

        item.setQuantity(
                quantity
        );

        item.setPriceType(
                PriceType.UNIT
        );

        item.setReferencePrice(
                unitPrice
        );

        item.setSubtotal(
                subtotal
        );

        item.setNotes(
                notes
        );

        OrderItem savedItem =
                orderItemRepository.save(item);

        recalcularTotales(order);

        return savedItem;
    }

    // =========================================================
    // RECALCULAR TOTALES
    // =========================================================

    private void recalcularTotales(
            Order order) {

        BigDecimal subtotal =
                orderItemRepository
                        .findByOrder_IdOrderByIdAsc(
                                order.getId()
                        )
                        .stream()
                        .map(
                                OrderItem::getSubtotal
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        order.setProductsSubtotal(
                subtotal
        );

        BigDecimal deliveryFee =
                order.getDeliveryFee() != null
                        ? order.getDeliveryFee()
                        : BigDecimal.ZERO;

        order.setTotal(
                subtotal.add(
                        deliveryFee
                )
        );

        orderRepository.save(order);
    }

    // =========================================================
    // ACTUALIZAR PEDIDO
    // =========================================================

    public Order actualizarPedido(
            Long orderId,
            UpdateOrderRequest request) {

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe el pedido con id: "
                                        + orderId
                        )
                );

        validarPedidoEditable(order);

        if (request.getCustomerName() == null
                || request.getCustomerName().isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre del cliente es obligatorio"
            );
        }

        boolean requiresDelivery =
                Boolean.TRUE.equals(
                        request.getRequiresDelivery()
                );

        BigDecimal deliveryFee =
                request.getDeliveryFee() != null
                        ? request.getDeliveryFee()
                        : BigDecimal.ZERO;

        if (deliveryFee.compareTo(
                BigDecimal.ZERO
        ) < 0) {

            throw new IllegalArgumentException(
                    "El costo de delivery no puede ser negativo"
            );
        }

        /*
         * REGLA DE NEGOCIO:
         *
         * Dirección y distrito son datos del lugar
         * de entrega y no dependen del costo de delivery.
         *
         * requiresDelivery solamente indica si
         * se debe aplicar deliveryFee.
         */
        if (!requiresDelivery) {
            deliveryFee = BigDecimal.ZERO;
        }

        order.setCustomerName(
                request.getCustomerName()
        );

        order.setDeliveryDate(
                request.getDeliveryDate()
        );

        order.setDeliveryTime(
                request.getDeliveryTime()
        );

        /*
         * Estos datos se guardan aunque
         * requiresDelivery sea false.
         */
        order.setAddress(
                request.getAddress()
        );

        order.setDistrict(
                request.getDistrict()
        );

        order.setRequiresDelivery(
                requiresDelivery
        );

        order.setDeliveryFee(
                deliveryFee
        );

        order.setObservations(
                request.getObservations()
        );

        BigDecimal productsSubtotal =
                order.getProductsSubtotal() != null
                        ? order.getProductsSubtotal()
                        : BigDecimal.ZERO;

        order.setTotal(
                productsSubtotal.add(
                        deliveryFee
                )
        );

        return orderRepository.save(order);
    }

    // =========================================================
    // ELIMINAR ITEM
    // =========================================================

    public void eliminarItem(
            Long orderId,
            Long itemId) {

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe el pedido con id: "
                                        + orderId
                        )
                );

        validarPedidoEditable(order);

        OrderItem item =
                orderItemRepository
                        .findByIdAndOrder_Id(
                                itemId,
                                orderId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No existe el item "
                                                + itemId
                                                + " en el pedido "
                                                + orderId
                                )
                        );

        orderItemRepository.delete(item);

        orderItemRepository.flush();

        recalcularTotales(order);
    }

    // =========================================================
    // CAMBIAR ESTADO
    // =========================================================

    public Order cambiarEstado(
            Long orderId,
            OrderStatus nuevoEstado) {

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe el pedido con id: "
                                        + orderId
                        )
                );

        OrderStatus estadoActual =
                order.getStatus();

        /*
         * Para confirmar un pedido
         * debe existir al menos un producto.
         */
        if (estadoActual == OrderStatus.DRAFT
                && nuevoEstado == OrderStatus.CONFIRMED) {

            boolean tieneItems =
                    !orderItemRepository
                            .findByOrder_IdOrderByIdAsc(
                                    orderId
                            )
                            .isEmpty();

            if (!tieneItems) {
                throw new BusinessRuleException(
                        "No se puede confirmar un pedido sin productos"
                );
            }
        }

        if (estadoActual == nuevoEstado) {
            throw new BusinessRuleException(
                    "El pedido ya se encuentra en estado "
                            + estadoActual
            );
        }

        if (!esTransicionValida(
                estadoActual,
                nuevoEstado
        )) {

            throw new BusinessRuleException(
                    "No se permite cambiar el pedido de "
                            + estadoActual
                            + " a "
                            + nuevoEstado
            );
        }

        order.setStatus(
                nuevoEstado
        );

        return orderRepository.save(order);
    }

    // =========================================================
    // VALIDAR TRANSICIÓN DE ESTADO
    // =========================================================

    private boolean esTransicionValida(
            OrderStatus estadoActual,
            OrderStatus nuevoEstado) {

        return switch (estadoActual) {

            case DRAFT ->
                    nuevoEstado
                            == OrderStatus.CONFIRMED
                            || nuevoEstado
                            == OrderStatus.CANCELLED;

            case CONFIRMED ->
                    nuevoEstado
                            == OrderStatus.PREPARING
                            || nuevoEstado
                            == OrderStatus.CANCELLED;

            case PREPARING ->
                    nuevoEstado
                            == OrderStatus.DELIVERED
                            || nuevoEstado
                            == OrderStatus.CANCELLED;

            case DELIVERED, CANCELLED ->
                    false;
        };
    }

    // =========================================================
    // LISTAR PEDIDOS
    // =========================================================

    public List<Order> listarPedidos() {

        return orderRepository.findAll(
                Sort.by(
                        Sort.Direction.DESC,
                        "id"
                )
        );
    }

    // =========================================================
    // LISTAR PEDIDOS FILTRADOS
    // =========================================================

    public List<Order> listarPedidosFiltrados(
            OrderStatus status,
            OrderSource source,
            String customer) {

        Specification<Order> specification =
                (
                        root,
                        query,
                        criteriaBuilder
                ) ->
                        criteriaBuilder.conjunction();

        if (status != null) {

            specification =
                    specification.and(
                            (
                                    root,
                                    query,
                                    criteriaBuilder
                            ) ->
                                    criteriaBuilder.equal(
                                            root.get("status"),
                                            status
                                    )
                    );
        }

        if (source != null) {

            specification =
                    specification.and(
                            (
                                    root,
                                    query,
                                    criteriaBuilder
                            ) ->
                                    criteriaBuilder.equal(
                                            root.get("source"),
                                            source
                                    )
                    );
        }

        if (customer != null
                && !customer.isBlank()) {

            String customerFilter =
                    "%"
                            + customer.toLowerCase()
                            + "%";

            specification =
                    specification.and(
                            (
                                    root,
                                    query,
                                    criteriaBuilder
                            ) ->
                                    criteriaBuilder.like(
                                            criteriaBuilder.lower(
                                                    root.get(
                                                            "customerName"
                                                    )
                                            ),
                                            customerFilter
                                    )
                    );
        }

        return orderRepository.findAll(
                specification,
                Sort.by(
                        Sort.Direction.DESC,
                        "id"
                )
        );
    }

    // =========================================================
    // VALIDAR QUE EL PEDIDO SEA EDITABLE
    // =========================================================

    private void validarPedidoEditable(
            Order order) {

        if (order.getStatus()
                != OrderStatus.DRAFT) {

            throw new BusinessRuleException(
                    "El pedido no puede modificarse porque su estado actual es "
                            + order.getStatus()
            );
        }
    }

    // =========================================================
    // LISTAR / FILTRAR / PAGINAR PEDIDOS
    // =========================================================

    public Page<Order> listarPedidosPaginados(
            OrderStatus status,
            OrderSource source,
            String customer,
            Pageable pageable) {

        Specification<Order> specification =
                (
                        root,
                        query,
                        criteriaBuilder
                ) ->
                        criteriaBuilder.conjunction();

        if (status != null) {

            specification =
                    specification.and(
                            (
                                    root,
                                    query,
                                    criteriaBuilder
                            ) ->
                                    criteriaBuilder.equal(
                                            root.get("status"),
                                            status
                                    )
                    );
        }

        if (source != null) {

            specification =
                    specification.and(
                            (
                                    root,
                                    query,
                                    criteriaBuilder
                            ) ->
                                    criteriaBuilder.equal(
                                            root.get("source"),
                                            source
                                    )
                    );
        }

        if (customer != null
                && !customer.isBlank()) {

            String customerFilter =
                    "%"
                            + customer.toLowerCase()
                            + "%";

            specification =
                    specification.and(
                            (
                                    root,
                                    query,
                                    criteriaBuilder
                            ) ->
                                    criteriaBuilder.like(
                                            criteriaBuilder.lower(
                                                    root.get(
                                                            "customerName"
                                                    )
                                            ),
                                            customerFilter
                                    )
                    );
        }

        return orderRepository.findAll(
                specification,
                pageable
        );
    }
}
