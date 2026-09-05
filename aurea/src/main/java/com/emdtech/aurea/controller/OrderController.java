package com.emdtech.aurea.controller;

import com.emdtech.aurea.dto.AddCatalogItemRequest;
import com.emdtech.aurea.dto.AddManualItemRequest;
import com.emdtech.aurea.dto.CreateOrderRequest;
import com.emdtech.aurea.dto.OrderDetailResponse;
import com.emdtech.aurea.dto.OrderItemResponse;
import com.emdtech.aurea.dto.OrderResponse;
import com.emdtech.aurea.dto.PagedOrderResponse;
import com.emdtech.aurea.dto.UpdateOrderRequest;
import com.emdtech.aurea.dto.UpdateOrderStatusRequest;

import com.emdtech.aurea.exception.BusinessRuleException;

import com.emdtech.aurea.order.Order;
import com.emdtech.aurea.order.OrderItem;
import com.emdtech.aurea.order.OrderSource;
import com.emdtech.aurea.order.OrderStatus;

import com.emdtech.aurea.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/orders")
@Tag(
        name = "Pedidos",
        description = "Operaciones para crear, consultar, modificar y gestionar pedidos de AUREA"
)
public class OrderController {

    private final OrderService orderService;


    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    // =========================================================
    // CREAR PEDIDO
    // =========================================================

    @Operation(
            summary = "Crear un nuevo pedido",
            description = "Crea un pedido en estado DRAFT con los datos generales del cliente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Pedido creado correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos"
            )
    })
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        Order order = orderService.crearPedido(
                request.getCustomerName(),
                request.isRequiresDelivery(),
                request.getAddress(),
                request.getDistrict(),
                request.getDeliveryFee(),
                request.getSource(),
                request.getDeliveryDate(),
                request.getDeliveryTime(),
                request.getObservations()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(order));
    }


    // =========================================================
    // AGREGAR PRODUCTO DE CATÁLOGO
    // =========================================================

    @Operation(
            summary = "Agregar producto de catálogo",
            description = "Agrega al pedido un producto utilizando uno de los precios registrados en el catálogo."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Producto agregado correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "El pedido no puede modificarse o los datos son inválidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pedido o precio de producto no encontrado"
            )
    })
    @PostMapping("/{orderId}/catalog-items")
    public ResponseEntity<OrderItemResponse> addCatalogItem(
            @PathVariable Long orderId,
            @Valid @RequestBody AddCatalogItemRequest request) {

        OrderItem item =
                orderService.agregarProductoCatalogo(
                        orderId,
                        request.getProductPriceId()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toItemResponse(item));
    }


    // =========================================================
    // CONSULTAR PEDIDO
    // =========================================================

    @Operation(
            summary = "Consultar detalle de un pedido",
            description = "Devuelve los datos generales del pedido y todos sus items."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pedido encontrado"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pedido no encontrado"
            )
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrder(
            @PathVariable Long orderId) {

        Order order =
                orderService.obtenerPedido(orderId);

        List<OrderItem> items =
                orderService.obtenerItemsPedido(orderId);

        OrderDetailResponse response =
                new OrderDetailResponse();

        response.setId(order.getId());
        response.setCustomerName(order.getCustomerName());
        response.setDeliveryDate(order.getDeliveryDate());
        response.setDeliveryTime(order.getDeliveryTime());
        response.setRequiresDelivery(order.isRequiresDelivery());
        response.setAddress(order.getAddress());
        response.setDistrict(order.getDistrict());
        response.setDeliveryFee(order.getDeliveryFee());
        response.setProductsSubtotal(order.getProductsSubtotal());
        response.setTotal(order.getTotal());
        response.setObservations(order.getObservations());
        response.setStatus(order.getStatus());
        response.setSource(order.getSource());

        response.setItems(
                items.stream()
                        .map(this::toItemResponse)
                        .toList()
        );

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // AGREGAR PRODUCTO MANUAL
    // =========================================================

    @Operation(
            summary = "Agregar producto manual",
            description = "Agrega un item que no pertenece necesariamente al catálogo de productos."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Item manual agregado correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "El pedido no puede modificarse o los datos son inválidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pedido no encontrado"
            )
    })
    @PostMapping("/{orderId}/manual-items")
    public ResponseEntity<OrderItemResponse> addManualItem(
            @PathVariable Long orderId,
            @Valid @RequestBody AddManualItemRequest request) {

        OrderItem item =
                orderService.agregarProductoManual(
                        orderId,
                        request.getDescription(),
                        request.getQuantity(),
                        request.getUnitPrice(),
                        request.getNotes()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toItemResponse(item));
    }


    // =========================================================
    // ACTUALIZAR PEDIDO
    // =========================================================

    @Operation(
            summary = "Actualizar datos de un pedido",
            description = "Actualiza los datos generales de un pedido. Solo los pedidos en estado DRAFT pueden modificarse."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pedido actualizado correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Regla de negocio o datos de entrada inválidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pedido no encontrado"
            )
    })
    @PutMapping("/{orderId}")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderRequest request) {

        Order order =
                orderService.actualizarPedido(
                        orderId,
                        request
                );

        return ResponseEntity.ok(
                toResponse(order)
        );
    }


    // =========================================================
    // ELIMINAR ITEM
    // =========================================================

    @Operation(
            summary = "Eliminar item de un pedido",
            description = "Elimina un item y recalcula automáticamente el subtotal y total del pedido. Solo se permite cuando el pedido está en DRAFT."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Item eliminado correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "El pedido no puede modificarse"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pedido o item no encontrado"
            )
    })
    @DeleteMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<Void> deleteOrderItem(
            @PathVariable Long orderId,
            @PathVariable Long itemId) {

        orderService.eliminarItem(
                orderId,
                itemId
        );

        return ResponseEntity
                .noContent()
                .build();
    }


    // =========================================================
    // CAMBIAR ESTADO
    // =========================================================

    @Operation(
            summary = "Cambiar estado de un pedido",
            description = """
                    Cambia el estado siguiendo las reglas del ciclo de vida:

                    DRAFT -> CONFIRMED
                    CONFIRMED -> PREPARING
                    PREPARING -> DELIVERED

                    También se permite cancelar desde:
                    DRAFT, CONFIRMED o PREPARING.

                    DELIVERED y CANCELLED son estados finales.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Estado actualizado correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Transición de estado no permitida"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pedido no encontrado"
            )
    })
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {

        Order order =
                orderService.cambiarEstado(
                        orderId,
                        request.getStatus()
                );

        return ResponseEntity.ok(
                toResponse(order)
        );
    }


    // =========================================================
    // LISTAR / FILTRAR / PAGINAR
    // =========================================================

    @Operation(
            summary = "Listar pedidos",
            description = """
                    Lista los pedidos con paginación.

                    Permite filtrar opcionalmente por:
                    - estado
                    - origen
                    - nombre del cliente

                    Los pedidos se ordenan por id descendente.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado obtenido correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Filtros o parámetros de paginación inválidos"
            )
    })
    @GetMapping
    public ResponseEntity<PagedOrderResponse> listOrders(

            @RequestParam(required = false)
            OrderStatus status,

            @RequestParam(required = false)
            OrderSource source,

            @RequestParam(required = false)
            String customer,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        if (page < 0) {

            throw new BusinessRuleException(
                    "El número de página no puede ser negativo"
            );
        }

        if (size < 1 || size > 100) {

            throw new BusinessRuleException(
                    "El tamaño de página debe estar entre 1 y 100"
            );
        }

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                Sort.Direction.DESC,
                                "id"
                        )
                );

        Page<Order> orderPage =
                orderService.listarPedidosPaginados(
                        status,
                        source,
                        customer,
                        pageable
                );

        List<OrderResponse> content =
                orderPage
                        .getContent()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        PagedOrderResponse response =
                new PagedOrderResponse();

        response.setContent(content);

        response.setPage(
                orderPage.getNumber()
        );

        response.setSize(
                orderPage.getSize()
        );

        response.setTotalElements(
                orderPage.getTotalElements()
        );

        response.setTotalPages(
                orderPage.getTotalPages()
        );

        response.setFirst(
                orderPage.isFirst()
        );

        response.setLast(
                orderPage.isLast()
        );

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // MAPPERS
    // =========================================================

    private OrderResponse toResponse(
            Order order) {

        OrderResponse response =
                new OrderResponse();

        response.setId(order.getId());
        response.setCustomerName(order.getCustomerName());
        response.setDeliveryDate(order.getDeliveryDate());
        response.setDeliveryTime(order.getDeliveryTime());
        response.setRequiresDelivery(order.isRequiresDelivery());
        response.setDistrict(order.getDistrict());
        response.setDeliveryFee(order.getDeliveryFee());
        response.setProductsSubtotal(order.getProductsSubtotal());
        response.setTotal(order.getTotal());
        response.setStatus(order.getStatus());
        response.setSource(order.getSource());

        return response;
    }


    private OrderItemResponse toItemResponse(
            OrderItem item) {

        OrderItemResponse response =
                new OrderItemResponse();

        response.setId(item.getId());
        response.setOrderId(
                item.getOrder().getId()
        );

        response.setItemType(
                item.getItemType()
        );

        response.setDescription(
                item.getDescriptionSnapshot()
        );

        response.setQuantity(
                item.getQuantity()
        );

        response.setPriceType(
                item.getPriceType()
        );

        response.setReferencePrice(
                item.getReferencePrice()
        );

        response.setSubtotal(
                item.getSubtotal()
        );

        if (item.getProduct() != null) {

            response.setProductId(
                    item.getProduct().getId()
            );
        }

        if (item.getProductPrice() != null) {

            response.setProductPriceId(
                    item.getProductPrice().getId()
            );
        }

        return response;
    }
}
