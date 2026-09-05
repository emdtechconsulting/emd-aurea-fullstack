package com.emdtech.aurea.service;

import com.emdtech.aurea.order.Order;
import com.emdtech.aurea.order.OrderSource;
import com.emdtech.aurea.order.OrderStatus;
import com.emdtech.aurea.repository.OrderRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.test.context.ActiveProfiles;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderFilteringIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;


    @BeforeEach
    void setUp() {

        orderRepository.deleteAll();

        crearPedido(
                "Ana Torres",
                OrderSource.WEB,
                OrderStatus.DRAFT
        );

        crearPedido(
                "Carlos Pérez",
                OrderSource.MOBILE,
                OrderStatus.CONFIRMED
        );

        crearPedido(
                "Ana María",
                OrderSource.WEB,
                OrderStatus.CONFIRMED
        );

        crearPedido(
                "Luis Gómez",
                OrderSource.MANUAL,
                OrderStatus.DRAFT
        );

        crearPedido(
                "María López",
                OrderSource.WEB,
                OrderStatus.CANCELLED
        );
    }


    @Test
    void listarSinFiltrosDebeDevolverTodosLosPedidos() {

        Pageable pageable =
                PageRequest.of(
                        0,
                        10,
                        Sort.by(
                                Sort.Direction.DESC,
                                "id"
                        )
                );

        Page<Order> resultado =
                orderService.listarPedidosPaginados(
                        null,
                        null,
                        null,
                        pageable
                );

        assertEquals(
                5,
                resultado.getTotalElements()
        );

        assertEquals(
                5,
                resultado.getContent().size()
        );
    }


    @Test
    void filtrarPorStatusConfirmedDebeDevolverDosPedidos() {

        Pageable pageable =
                pageable10();

        Page<Order> resultado =
                orderService.listarPedidosPaginados(
                        OrderStatus.CONFIRMED,
                        null,
                        null,
                        pageable
                );

        assertEquals(
                2,
                resultado.getTotalElements()
        );

        assertTrue(
                resultado.getContent()
                        .stream()
                        .allMatch(order ->
                                order.getStatus()
                                        == OrderStatus.CONFIRMED
                        )
        );
    }


    @Test
    void filtrarPorSourceWebDebeDevolverTresPedidos() {

        Page<Order> resultado =
                orderService.listarPedidosPaginados(
                        null,
                        OrderSource.WEB,
                        null,
                        pageable10()
                );

        assertEquals(
                3,
                resultado.getTotalElements()
        );

        assertTrue(
                resultado.getContent()
                        .stream()
                        .allMatch(order ->
                                order.getSource()
                                        == OrderSource.WEB
                        )
        );
    }


    @Test
    void filtrarCustomerAnaDebeIgnorarMayusculasYMinusculas() {

        Page<Order> resultado =
                orderService.listarPedidosPaginados(
                        null,
                        null,
                        "ANA",
                        pageable10()
                );

        assertEquals(
                2,
                resultado.getTotalElements()
        );

        assertTrue(
                resultado.getContent()
                        .stream()
                        .allMatch(order ->
                                order.getCustomerName()
                                        .toLowerCase()
                                        .contains("ana")
                        )
        );
    }


    @Test
    void combinarStatusSourceYCustomerDebeAplicarAnd() {

        Page<Order> resultado =
                orderService.listarPedidosPaginados(
                        OrderStatus.CONFIRMED,
                        OrderSource.WEB,
                        "Ana",
                        pageable10()
                );

        assertEquals(
                1,
                resultado.getTotalElements()
        );

        Order order =
                resultado.getContent().get(0);

        assertEquals(
                "Ana María",
                order.getCustomerName()
        );

        assertEquals(
                OrderStatus.CONFIRMED,
                order.getStatus()
        );

        assertEquals(
                OrderSource.WEB,
                order.getSource()
        );
    }


    @Test
    void paginacionSizeDosDebeCrearTresPaginas() {

        Pageable pageable =
                PageRequest.of(
                        0,
                        2,
                        Sort.by(
                                Sort.Direction.DESC,
                                "id"
                        )
                );

        Page<Order> resultado =
                orderService.listarPedidosPaginados(
                        null,
                        null,
                        null,
                        pageable
                );

        assertEquals(
                5,
                resultado.getTotalElements()
        );

        assertEquals(
                3,
                resultado.getTotalPages()
        );

        assertEquals(
                2,
                resultado.getContent().size()
        );

        assertEquals(
                0,
                resultado.getNumber()
        );

        assertTrue(
                resultado.isFirst()
        );
    }


    @Test
    void segundaPaginaDebeDevolverDosElementos() {

        Pageable pageable =
                PageRequest.of(
                        1,
                        2,
                        Sort.by(
                                Sort.Direction.DESC,
                                "id"
                        )
                );

        Page<Order> resultado =
                orderService.listarPedidosPaginados(
                        null,
                        null,
                        null,
                        pageable
                );

        assertEquals(
                1,
                resultado.getNumber()
        );

        assertEquals(
                2,
                resultado.getContent().size()
        );

        assertEquals(
                5,
                resultado.getTotalElements()
        );

        assertEquals(
                3,
                resultado.getTotalPages()
        );
    }


    @Test
    void listadoDebeOrdenarsePorIdDescendente() {

        Page<Order> resultado =
                orderService.listarPedidosPaginados(
                        null,
                        null,
                        null,
                        pageable10()
                );

        for (int i = 0;
             i < resultado.getContent().size() - 1;
             i++) {

            Long actual =
                    resultado.getContent()
                            .get(i)
                            .getId();

            Long siguiente =
                    resultado.getContent()
                            .get(i + 1)
                            .getId();

            assertTrue(
                    actual > siguiente
            );
        }
    }


    private Pageable pageable10() {

        return PageRequest.of(
                0,
                10,
                Sort.by(
                        Sort.Direction.DESC,
                        "id"
                )
        );
    }


    private void crearPedido(
            String customerName,
            OrderSource source,
            OrderStatus status) {

        Order order =
                orderService.crearPedido(
                        customerName,
                        false,
                        null,
                        null,
                        BigDecimal.ZERO,
                        source
                );

        order.setStatus(status);

        orderRepository.save(order);
    }
}