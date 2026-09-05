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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductPriceRepository productPriceRepository;

    private OrderService orderService;


    @BeforeEach
    void setUp() {

        orderService =
                new OrderService(
                        orderRepository,
                        orderItemRepository,
                        productPriceRepository
                );
    }


    // ============================================================
    // CREAR PEDIDO
    // ============================================================

    @Test
    void crearPedidoSinCostoDeliveryDebeConservarDireccionYDistrito() {

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        Order resultado =
                orderService.crearPedido(
                        "Cliente Prueba",
                        false,
                        "Av. Los Cedros 500",
                        "Chorrillos",
                        new BigDecimal("15.00"),
                        OrderSource.WEB
                );

        assertEquals(
                "Cliente Prueba",
                resultado.getCustomerName()
        );

        assertFalse(
                resultado.isRequiresDelivery()
        );

        assertEquals(
                "Av. Los Cedros 500",
                resultado.getAddress()
        );

        assertEquals(
                "Chorrillos",
                resultado.getDistrict()
        );

        assertBigDecimalEquals(
                "0.00",
                resultado.getDeliveryFee()
        );

        assertBigDecimalEquals(
                "0.00",
                resultado.getProductsSubtotal()
        );

        assertBigDecimalEquals(
                "0.00",
                resultado.getTotal()
        );

        assertEquals(
                OrderStatus.DRAFT,
                resultado.getStatus()
        );

        assertEquals(
                OrderSource.WEB,
                resultado.getSource()
        );

        verify(orderRepository)
                .save(any(Order.class));
    }


    @Test
    void crearPedidoConDeliveryDebeConservarDatosDelivery() {

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        Order resultado =
                orderService.crearPedido(
                        "Cliente Delivery",
                        true,
                        "Av. Principal 123",
                        "Chorrillos",
                        new BigDecimal("10.00"),
                        OrderSource.MOBILE
                );

        assertTrue(
                resultado.isRequiresDelivery()
        );

        assertEquals(
                "Av. Principal 123",
                resultado.getAddress()
        );

        assertEquals(
                "Chorrillos",
                resultado.getDistrict()
        );

        assertBigDecimalEquals(
                "10.00",
                resultado.getDeliveryFee()
        );

        assertBigDecimalEquals(
                "10.00",
                resultado.getTotal()
        );

        assertEquals(
                OrderStatus.DRAFT,
                resultado.getStatus()
        );

        assertEquals(
                OrderSource.MOBILE,
                resultado.getSource()
        );
    }


    @Test
    void crearPedidoConSourceNuloDebeUsarManual() {

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        Order resultado =
                orderService.crearPedido(
                        "Cliente Manual",
                        false,
                        null,
                        null,
                        null,
                        null
                );

        assertEquals(
                OrderSource.MANUAL,
                resultado.getSource()
        );

        assertBigDecimalEquals(
                "0.00",
                resultado.getDeliveryFee()
        );
    }


    @Test
    void crearPedidoSinClienteDebeLanzarExcepcion() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        orderService.crearPedido(
                                "   ",
                                false,
                                null,
                                null,
                                BigDecimal.ZERO,
                                OrderSource.WEB
                        )
        );

        verify(orderRepository, never())
                .save(any(Order.class));
    }


    @Test
    void crearPedidoConCostoDeliverySinDireccionDebePermitirse() {

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        Order resultado =
                orderService.crearPedido(
                        "Cliente",
                        true,
                        null,
                        "Chorrillos",
                        new BigDecimal("10.00"),
                        OrderSource.WEB
                );

        assertTrue(
                resultado.isRequiresDelivery()
        );

        assertNull(
                resultado.getAddress()
        );

        assertEquals(
                "Chorrillos",
                resultado.getDistrict()
        );

        assertBigDecimalEquals(
                "10.00",
                resultado.getDeliveryFee()
        );

        assertBigDecimalEquals(
                "10.00",
                resultado.getTotal()
        );

        verify(orderRepository)
                .save(any(Order.class));
    }


    @Test
    void crearPedidoConDeliveryNegativoDebeLanzarExcepcion() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        orderService.crearPedido(
                                "Cliente",
                                true,
                                "Av. Prueba 123",
                                "Chorrillos",
                                new BigDecimal("-1.00"),
                                OrderSource.WEB
                        )
        );

        verify(orderRepository, never())
                .save(any(Order.class));
    }


    // ============================================================
    // ITEM MANUAL
    // ============================================================

    @Test
    void agregarProductoManualValidoDebeCrearItemCorrectamente() {

        Order order =
                crearPedidoDraft();

        when(orderRepository.findById(1L))
                .thenReturn(
                        Optional.of(order)
                );

        when(orderItemRepository.save(
                any(OrderItem.class)
        ))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        when(orderItemRepository
                .findByOrder_IdOrderByIdAsc(
                        any()
                ))
                .thenReturn(
                        Collections.emptyList()
                );

        OrderItem resultado =
                orderService.agregarProductoManual(
                        1L,
                        "Producto personalizado",
                        3,
                        new BigDecimal("5.00"),
                        "Sin cebolla"
                );

        assertSame(
                order,
                resultado.getOrder()
        );

        assertEquals(
                ItemType.MANUAL,
                resultado.getItemType()
        );

        assertNull(
                resultado.getProduct()
        );

        assertNull(
                resultado.getProductPrice()
        );

        assertEquals(
                "Producto personalizado",
                resultado.getDescriptionSnapshot()
        );

        assertEquals(
                3,
                resultado.getQuantity()
        );

        assertEquals(
                PriceType.UNIT,
                resultado.getPriceType()
        );

        assertBigDecimalEquals(
                "5.00",
                resultado.getReferencePrice()
        );

        assertBigDecimalEquals(
                "15.00",
                resultado.getSubtotal()
        );

        assertEquals(
                "Sin cebolla",
                resultado.getNotes()
        );
    }


    @Test
    void agregarProductoManualConCantidadCeroDebeFallar() {

        Order order =
                crearPedidoDraft();

        when(orderRepository.findById(1L))
                .thenReturn(
                        Optional.of(order)
                );

        assertThrows(
                BusinessRuleException.class,
                () ->
                        orderService.agregarProductoManual(
                                1L,
                                "Producto manual",
                                0,
                                new BigDecimal("5.00"),
                                null
                        )
        );

        verify(orderItemRepository, never())
                .save(any(OrderItem.class));
    }


    @Test
    void agregarProductoManualConPrecioNegativoDebeFallar() {

        Order order =
                crearPedidoDraft();

        when(orderRepository.findById(1L))
                .thenReturn(
                        Optional.of(order)
                );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        orderService.agregarProductoManual(
                                1L,
                                "Producto manual",
                                2,
                                new BigDecimal("-5.00"),
                                null
                        )
        );

        verify(orderItemRepository, never())
                .save(any(OrderItem.class));
    }


    @Test
    void pedidoConfirmedNoDebePermitirAgregarItemManual() {

        Order order =
                crearPedidoDraft();

        order.setStatus(
                OrderStatus.CONFIRMED
        );

        when(orderRepository.findById(1L))
                .thenReturn(
                        Optional.of(order)
                );

        assertThrows(
                BusinessRuleException.class,
                () ->
                        orderService.agregarProductoManual(
                                1L,
                                "Producto",
                                1,
                                new BigDecimal("10.00"),
                                null
                        )
        );

        verify(orderItemRepository, never())
                .save(any(OrderItem.class));
    }


    // ============================================================
    // ITEM DE CATÁLOGO
    // ============================================================

    @Test
    void agregarProductoCatalogoDebeCrearSnapshotYRecalcularTotal() {

        Order order =
                crearPedidoDraft();

        asignarId(
                order,
                1L
        );

        order.setRequiresDelivery(true);

        order.setDeliveryFee(
                new BigDecimal("10.00")
        );

        Product product =
                new Product();

        product.setName(
                "Triple Clásico"
        );

        ProductPrice productPrice =
                new ProductPrice();

        productPrice.setProduct(product);
        productPrice.setQuantity(50);

        productPrice.setPrice(
                new BigDecimal("70.00")
        );

        OrderItem itemExistente =
                crearItemManual(
                        order,
                        "Pedido especial",
                        3,
                        "5.00"
                );

        AtomicReference<OrderItem> itemGuardado =
                new AtomicReference<>();

        when(orderRepository.findById(1L))
                .thenReturn(
                        Optional.of(order)
                );

        when(productPriceRepository.findById(100L))
                .thenReturn(
                        Optional.of(productPrice)
                );

        when(orderItemRepository.save(
                any(OrderItem.class)
        ))
                .thenAnswer(invocation -> {

                    OrderItem item =
                            invocation.getArgument(0);

                    itemGuardado.set(item);

                    return item;
                });

        when(orderItemRepository
                .findByOrder_IdOrderByIdAsc(1L))
                .thenAnswer(invocation ->
                        List.of(
                                itemExistente,
                                itemGuardado.get()
                        )
                );

        when(orderRepository.save(order))
                .thenReturn(order);

        OrderItem resultado =
                orderService.agregarProductoCatalogo(
                        1L,
                        100L
                );

        assertSame(
                order,
                resultado.getOrder()
        );

        assertEquals(
                ItemType.CATALOG,
                resultado.getItemType()
        );

        assertSame(
                product,
                resultado.getProduct()
        );

        assertSame(
                productPrice,
                resultado.getProductPrice()
        );

        assertEquals(
                "Triple Clásico",
                resultado.getDescriptionSnapshot()
        );

        assertEquals(
                50,
                resultado.getQuantity()
        );

        assertEquals(
                PriceType.PACKAGE,
                resultado.getPriceType()
        );

        assertBigDecimalEquals(
                "70.00",
                resultado.getReferencePrice()
        );

        assertBigDecimalEquals(
                "70.00",
                resultado.getSubtotal()
        );

        assertBigDecimalEquals(
                "85.00",
                order.getProductsSubtotal()
        );

        assertBigDecimalEquals(
                "95.00",
                order.getTotal()
        );

        verify(orderRepository)
                .save(order);
    }


    @Test
    void agregarProductoCatalogoConPrecioInexistenteDebeFallar() {

        Order order =
                crearPedidoDraft();

        when(orderRepository.findById(1L))
                .thenReturn(
                        Optional.of(order)
                );

        when(productPriceRepository.findById(999L))
                .thenReturn(
                        Optional.empty()
                );

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        orderService.agregarProductoCatalogo(
                                1L,
                                999L
                        )
        );

        verify(orderItemRepository, never())
                .save(any(OrderItem.class));
    }


    @Test
    void pedidoConfirmedNoDebePermitirAgregarProductoCatalogo() {

        Order order =
                crearPedidoDraft();

        order.setStatus(
                OrderStatus.CONFIRMED
        );

        when(orderRepository.findById(1L))
                .thenReturn(
                        Optional.of(order)
                );

        assertThrows(
                BusinessRuleException.class,
                () ->
                        orderService.agregarProductoCatalogo(
                                1L,
                                100L
                        )
        );

        verify(productPriceRepository, never())
                .findById(anyLong());

        verify(orderItemRepository, never())
                .save(any(OrderItem.class));
    }


    // ============================================================
    // ELIMINAR ITEM Y RECALCULAR
    // ============================================================

    @Test
    void eliminarItemDebeEliminarFlushYRecalcularTotales() {

        Order order =
                crearPedidoDraft();

        asignarId(
                order,
                1L
        );

        order.setRequiresDelivery(true);

        order.setDeliveryFee(
                new BigDecimal("10.00")
        );

        OrderItem itemEliminar =
                crearItemManual(
                        order,
                        "Item a eliminar",
                        2,
                        "10.00"
                );

        OrderItem itemRestante =
                crearItemManual(
                        order,
                        "Item restante",
                        3,
                        "5.00"
                );

        when(orderRepository.findById(1L))
                .thenReturn(
                        Optional.of(order)
                );

        when(orderItemRepository
                .findByIdAndOrder_Id(
                        20L,
                        1L
                ))
                .thenReturn(
                        Optional.of(itemEliminar)
                );

        when(orderItemRepository
                .findByOrder_IdOrderByIdAsc(1L))
                .thenReturn(
                        List.of(itemRestante)
                );

        when(orderRepository.save(order))
                .thenReturn(order);

        orderService.eliminarItem(
                1L,
                20L
        );

        verify(orderItemRepository)
                .delete(itemEliminar);

        verify(orderItemRepository)
                .flush();

        assertBigDecimalEquals(
                "15.00",
                order.getProductsSubtotal()
        );

        assertBigDecimalEquals(
                "25.00",
                order.getTotal()
        );

        verify(orderRepository)
                .save(order);
    }


    @Test
    void eliminarItemInexistenteDebeLanzarResourceNotFoundException() {

        Order order =
                crearPedidoDraft();

        when(orderRepository.findById(1L))
                .thenReturn(
                        Optional.of(order)
                );

        when(orderItemRepository
                .findByIdAndOrder_Id(
                        999L,
                        1L
                ))
                .thenReturn(
                        Optional.empty()
                );

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        orderService.eliminarItem(
                                1L,
                                999L
                        )
        );

        verify(orderItemRepository, never())
                .delete(any(OrderItem.class));

        verify(orderItemRepository, never())
                .flush();
    }


    // ============================================================
    // ACTUALIZAR PEDIDO
    // ============================================================

    @Test
    void actualizarPedidoConDeliveryDebeActualizarDatosYTotal() {

        Order order =
                crearPedidoDraft();

        order.setProductsSubtotal(
                new BigDecimal("25.00")
        );

        UpdateOrderRequest request =
                crearUpdateRequestBase();

        request.setCustomerName(
                "Cliente Actualizado"
        );

        request.setRequiresDelivery(true);

        request.setAddress(
                "Av. Guardia Civil 500"
        );

        request.setDistrict(
                "Chorrillos"
        );

        request.setDeliveryFee(
                new BigDecimal("8.00")
        );

        request.setDeliveryDate(
                LocalDate.of(
                        2026,
                        8,
                        30
                )
        );

        request.setDeliveryTime(
                LocalTime.of(
                        15,
                        30
                )
        );

        request.setObservations(
                "Entregar después de las 3"
        );

        when(orderRepository.findById(1L))
                .thenReturn(
                        Optional.of(order)
                );

        when(orderRepository.save(order))
                .thenReturn(order);

        Order resultado =
                orderService.actualizarPedido(
                        1L,
                        request
                );

        assertEquals(
                "Cliente Actualizado",
                resultado.getCustomerName()
        );

        assertTrue(
                resultado.isRequiresDelivery()
        );

        assertEquals(
                "Av. Guardia Civil 500",
                resultado.getAddress()
        );

        assertEquals(
                "Chorrillos",
                resultado.getDistrict()
        );

        assertEquals(
                LocalDate.of(
                        2026,
                        8,
                        30
                ),
                resultado.getDeliveryDate()
        );

        assertEquals(
                LocalTime.of(
                        15,
                        30
                ),
                resultado.getDeliveryTime()
        );

        assertEquals(
                "Entregar después de las 3",
                resultado.getObservations()
        );

        assertBigDecimalEquals(
                "8.00",
                resultado.getDeliveryFee()
        );

        assertBigDecimalEquals(
                "25.00",
                resultado.getProductsSubtotal()
        );

        assertBigDecimalEquals(
                "33.00",
                resultado.getTotal()
        );

        verify(orderRepository)
                .save(order);
    }


    @Test
    void actualizarPedidoSinCostoDeliveryDebeConservarDireccionYDistrito() {

        Order order =
                crearPedidoDraft();

        order.setRequiresDelivery(true);

        order.setAddress(
                "Dirección anterior"
        );

        order.setDistrict(
                "Distrito anterior"
        );

        order.setDeliveryFee(
                new BigDecimal("15.00")
        );

        order.setProductsSubtotal(
                new BigDecimal("40.00")
        );

        UpdateOrderRequest request =
                crearUpdateRequestBase();

        request.setRequiresDelivery(false);

        request.setAddress(
                "Av. Los Cedros 500"
        );

        request.setDistrict(
                "Chorrillos"
        );

        request.setDeliveryFee(
                new BigDecimal("20.00")
        );

        when(orderRepository.findById(1L))
                .thenReturn(
                        Optional.of(order)
                );

        when(orderRepository.save(order))
                .thenReturn(order);

        Order resultado =
                orderService.actualizarPedido(
                        1L,
                        request
                );

        assertFalse(
                resultado.isRequiresDelivery()
        );

        assertEquals(
                "Av. Los Cedros 500",
                resultado.getAddress()
        );

        assertEquals(
                "Chorrillos",
                resultado.getDistrict()
        );

        assertBigDecimalEquals(
                "0.00",
                resultado.getDeliveryFee()
        );

        assertBigDecimalEquals(
                "40.00",
                resultado.getProductsSubtotal()
        );

        assertBigDecimalEquals(
                "40.00",
                resultado.getTotal()
        );
    }


    @Test
    void actualizarPedidoConCostoDeliverySinDireccionDebePermitirse() {

        Order order =
                crearPedidoDraft();

        order.setProductsSubtotal(
                new BigDecimal("25.00")
        );

        UpdateOrderRequest request =
                crearUpdateRequestBase();

        request.setRequiresDelivery(true);

        request.setAddress(null);

        request.setDistrict(
                "Chorrillos"
        );

        request.setDeliveryFee(
                new BigDecimal("8.00")
        );

        when(orderRepository.findById(1L))
                .thenReturn(
                        Optional.of(order)
                );

        when(orderRepository.save(order))
                .thenReturn(order);

        Order resultado =
                orderService.actualizarPedido(
                        1L,
                        request
                );

        assertTrue(
                resultado.isRequiresDelivery()
        );

        assertNull(
                resultado.getAddress()
        );

        assertEquals(
                "Chorrillos",
                resultado.getDistrict()
        );

        assertBigDecimalEquals(
                "8.00",
                resultado.getDeliveryFee()
        );

        assertBigDecimalEquals(
                "33.00",
                resultado.getTotal()
        );

        verify(orderRepository)
                .save(order);
    }


    @Test
    void pedidoConfirmedNoDebePoderActualizarse() {

        Order order =
                crearPedidoDraft();

        order.setStatus(
                OrderStatus.CONFIRMED
        );

        UpdateOrderRequest request =
                crearUpdateRequestBase();

        when(orderRepository.findById(1L))
                .thenReturn(
                        Optional.of(order)
                );

        assertThrows(
                BusinessRuleException.class,
                () ->
                        orderService.actualizarPedido(
                                1L,
                                request
                        )
        );

        verify(orderRepository, never())
                .save(order);
    }


    // ============================================================
    // ESTADOS DEL PEDIDO
    // ============================================================

    @Test
    void confirmarPedidoVacioDebeFallar() {

        Order order =
                crearPedidoDraft();

        when(orderRepository.findById(1L))
                .thenReturn(
                        Optional.of(order)
                );

        when(orderItemRepository
                .findByOrder_IdOrderByIdAsc(1L))
                .thenReturn(
                        Collections.emptyList()
                );

        assertThrows(
                BusinessRuleException.class,
                () ->
                        orderService.cambiarEstado(
                                1L,
                                OrderStatus.CONFIRMED
                        )
        );

        assertEquals(
                OrderStatus.DRAFT,
                order.getStatus()
        );

        verify(orderRepository, never())
                .save(order);
    }


    @Test
    void pedidoDraftConItemDebePoderConfirmarse() {

        Order order =
                crearPedidoDraft();

        OrderItem item =
                crearItemManual(
                        order,
                        "Producto prueba",
                        1,
                        "10.00"
                );

        when(orderRepository.findById(1L))
                .thenReturn(
                        Optional.of(order)
                );

        when(orderItemRepository
                .findByOrder_IdOrderByIdAsc(1L))
                .thenReturn(
                        List.of(item)
                );

        when(orderRepository.save(order))
                .thenReturn(order);

        Order resultado =
                orderService.cambiarEstado(
                        1L,
                        OrderStatus.CONFIRMED
                );

        assertEquals(
                OrderStatus.CONFIRMED,
                resultado.getStatus()
        );

        verify(orderRepository)
                .save(order);
    }


    @Test
    void transicionDraftAPreparingDebeFallar() {

        Order order =
                crearPedidoDraft();

        when(orderRepository.findById(1L))
                .thenReturn(
                        Optional.of(order)
                );

        assertThrows(
                BusinessRuleException.class,
                () ->
                        orderService.cambiarEstado(
                                1L,
                                OrderStatus.PREPARING
                        )
        );

        assertEquals(
                OrderStatus.DRAFT,
                order.getStatus()
        );

        verify(orderRepository, never())
                .save(order);
    }


    @Test
    void confirmedDebePoderPasarAPreparing() {

        Order order =
                crearPedidoConEstado(
                        OrderStatus.CONFIRMED
                );

        when(orderRepository.findById(1L))
                .thenReturn(
                        Optional.of(order)
                );

        when(orderRepository.save(order))
                .thenReturn(order);

        Order resultado =
                orderService.cambiarEstado(
                        1L,
                        OrderStatus.PREPARING
                );

        assertEquals(
                OrderStatus.PREPARING,
                resultado.getStatus()
        );

        verify(orderRepository)
                .save(order);
    }


    @Test
    void preparingDebePoderPasarADelivered() {

        Order order =
                crearPedidoConEstado(
                        OrderStatus.PREPARING
                );

        when(orderRepository.findById(1L))
                .thenReturn(
                        Optional.of(order)
                );

        when(orderRepository.save(order))
                .thenReturn(order);

        Order resultado =
                orderService.cambiarEstado(
                        1L,
                        OrderStatus.DELIVERED
                );

        assertEquals(
                OrderStatus.DELIVERED,
                resultado.getStatus()
        );

        verify(orderRepository)
                .save(order);
    }


    @Test
    void draftDebePoderCancelarse() {

        Order order =
                crearPedidoDraft();

        when(orderRepository.findById(1L))
                .thenReturn(
                        Optional.of(order)
                );

        when(orderRepository.save(order))
                .thenReturn(order);

        Order resultado =
                orderService.cambiarEstado(
                        1L,
                        OrderStatus.CANCELLED
                );

        assertEquals(
                OrderStatus.CANCELLED,
                resultado.getStatus()
        );
    }


    @Test
    void confirmedDebePoderCancelarse() {

        Order order =
                crearPedidoConEstado(
                        OrderStatus.CONFIRMED
                );

        when(orderRepository.findById(1L))
                .thenReturn(
                        Optional.of(order)
                );

        when(orderRepository.save(order))
                .thenReturn(order);

        Order resultado =
                orderService.cambiarEstado(
                        1L,
                        OrderStatus.CANCELLED
                );

        assertEquals(
                OrderStatus.CANCELLED,
                resultado.getStatus()
        );
    }


    @Test
    void preparingDebePoderCancelarse() {

        Order order =
                crearPedidoConEstado(
                        OrderStatus.PREPARING
                );

        when(orderRepository.findById(1L))
                .thenReturn(
                        Optional.of(order)
                );

        when(orderRepository.save(order))
                .thenReturn(order);

        Order resultado =
                orderService.cambiarEstado(
                        1L,
                        OrderStatus.CANCELLED
                );

        assertEquals(
                OrderStatus.CANCELLED,
                resultado.getStatus()
        );
    }


    @Test
    void deliveredNoDebePermitirMasTransiciones() {

        Order order =
                crearPedidoConEstado(
                        OrderStatus.DELIVERED
                );

        when(orderRepository.findById(1L))
                .thenReturn(
                        Optional.of(order)
                );

        assertThrows(
                BusinessRuleException.class,
                () ->
                        orderService.cambiarEstado(
                                1L,
                                OrderStatus.CANCELLED
                        )
        );

        assertEquals(
                OrderStatus.DELIVERED,
                order.getStatus()
        );

        verify(orderRepository, never())
                .save(order);
    }


    @Test
    void cancelledNoDebePermitirMasTransiciones() {

        Order order =
                crearPedidoConEstado(
                        OrderStatus.CANCELLED
                );

        when(orderRepository.findById(1L))
                .thenReturn(
                        Optional.of(order)
                );

        assertThrows(
                BusinessRuleException.class,
                () ->
                        orderService.cambiarEstado(
                                1L,
                                OrderStatus.DRAFT
                        )
        );

        assertEquals(
                OrderStatus.CANCELLED,
                order.getStatus()
        );

        verify(orderRepository, never())
                .save(order);
    }


    @Test
    void cambiarAlMismoEstadoDebeFallar() {

        Order order =
                crearPedidoConEstado(
                        OrderStatus.CONFIRMED
                );

        when(orderRepository.findById(1L))
                .thenReturn(
                        Optional.of(order)
                );

        assertThrows(
                BusinessRuleException.class,
                () ->
                        orderService.cambiarEstado(
                                1L,
                                OrderStatus.CONFIRMED
                        )
        );

        assertEquals(
                OrderStatus.CONFIRMED,
                order.getStatus()
        );

        verify(orderRepository, never())
                .save(order);
    }


    // ============================================================
    // MÉTODOS AUXILIARES DE TEST
    // ============================================================

    private Order crearPedidoDraft() {

        return crearPedidoConEstado(
                OrderStatus.DRAFT
        );
    }


    private Order crearPedidoConEstado(
            OrderStatus status) {

        Order order =
                new Order();

        order.setCustomerName(
                "Cliente Test"
        );

        order.setRequiresDelivery(
                false
        );

        order.setDeliveryFee(
                BigDecimal.ZERO
        );

        order.setProductsSubtotal(
                BigDecimal.ZERO
        );

        order.setTotal(
                BigDecimal.ZERO
        );

        order.setStatus(
                status
        );

        order.setSource(
                OrderSource.MANUAL
        );

        return order;
    }


    private UpdateOrderRequest crearUpdateRequestBase() {

        UpdateOrderRequest request =
                new UpdateOrderRequest();

        request.setCustomerName(
                "Cliente Test"
        );

        request.setRequiresDelivery(
                false
        );

        request.setDeliveryFee(
                BigDecimal.ZERO
        );

        return request;
    }


    private OrderItem crearItemManual(
            Order order,
            String description,
            int quantity,
            String unitPrice) {

        BigDecimal price =
                new BigDecimal(unitPrice);

        OrderItem item =
                new OrderItem();

        item.setOrder(order);

        item.setItemType(
                ItemType.MANUAL
        );

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
                price
        );

        item.setSubtotal(
                price.multiply(
                        BigDecimal.valueOf(quantity)
                )
        );

        return item;
    }


    private void asignarId(
            Order order,
            Long id) {

        try {

            Field field =
                    Order.class
                            .getDeclaredField("id");

            field.setAccessible(true);

            field.set(
                    order,
                    id
            );

        } catch (ReflectiveOperationException exception) {

            throw new IllegalStateException(
                    "No se pudo asignar el ID del pedido durante la prueba",
                    exception
            );
        }
    }


    private void assertBigDecimalEquals(
            String expected,
            BigDecimal actual) {

        assertEquals(
                0,
                new BigDecimal(expected)
                        .compareTo(actual)
        );
    }
}