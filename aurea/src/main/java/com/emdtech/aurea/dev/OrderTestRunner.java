package com.emdtech.aurea.dev;

import com.emdtech.aurea.entity.ProductPrice;
import com.emdtech.aurea.order.Order;
import com.emdtech.aurea.order.OrderItem;
import com.emdtech.aurea.order.OrderSource;
import com.emdtech.aurea.repository.ProductPriceRepository;
import com.emdtech.aurea.service.OrderService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderTestRunner implements CommandLineRunner {

    private final OrderService orderService;
    private final ProductPriceRepository productPriceRepository;

    @Value("${aurea.order.test.enabled:false}")
    private boolean enabled;

    public OrderTestRunner(
            OrderService orderService,
            ProductPriceRepository productPriceRepository) {

        this.orderService = orderService;
        this.productPriceRepository = productPriceRepository;
    }

    @Override
    public void run(String... args) {

        if (!enabled) {
            return;
        }

        System.out.println();
        System.out.println("==========================================");
        System.out.println("AUREA - ORDER TEST");
        System.out.println("==========================================");

        ProductPrice productPrice = productPriceRepository
                .findAll()
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No existen precios en el catálogo"
                        )
                );

        Order order = orderService.crearPedido(
                "Cliente de prueba AUREA",
                true,
                "Av. Prueba 123",
                "Chorrillos",
                new BigDecimal("8.00"),
                OrderSource.MANUAL
        );

        System.out.println(
                "Pedido creado con ID: " + order.getId()
        );

        OrderItem catalogItem =
                orderService.agregarProductoCatalogo(
                        order.getId(),
                        productPrice.getId()
                );

        System.out.println(
                "Producto catálogo agregado: "
                        + catalogItem.getDescriptionSnapshot()
        );

        System.out.println(
                "Precio paquete: S/ "
                        + catalogItem.getSubtotal()
        );

        OrderItem manualItem =
                orderService.agregarProductoManual(
                        order.getId(),
                        "Decoración especial de prueba",
                        3,
                        new BigDecimal("5.00"),
                        "Ítem manual del laboratorio"
                );

        System.out.println(
                "Producto manual agregado: "
                        + manualItem.getDescriptionSnapshot()
        );

        System.out.println(
                "Subtotal manual: S/ "
                        + manualItem.getSubtotal()
        );

        System.out.println("==========================================");
        System.out.println("TEST COMPLETADO");
        System.out.println("==========================================");
        System.out.println();
    }
}