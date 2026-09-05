package com.emdtech.aurea.controller;

import com.emdtech.aurea.exception.GlobalExceptionHandler;
import com.emdtech.aurea.service.OrderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verifyNoInteractions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class OrderControllerValidationTest {

    @Mock
    private OrderService orderService;

    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {

        OrderController controller =
                new OrderController(orderService);

        mockMvc =
                MockMvcBuilders
                        .standaloneSetup(controller)
                        .setControllerAdvice(
                                new GlobalExceptionHandler()
                        )
                        .build();
    }


    // ============================================================
    // CREATE ORDER
    // ============================================================

    @Test
    void crearPedidoConClienteVacioDebeResponder400()
            throws Exception {

        String json = """
                {
                    "customerName": "",
                    "requiresDelivery": false,
                    "deliveryFee": 0,
                    "source": "WEB"
                }
                """;

        mockMvc.perform(
                        post("/api/orders")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(json)
                )
                .andExpect(
                        status().isBadRequest()
                );

        verifyNoInteractions(
                orderService
        );
    }


    @Test
    void crearPedidoConDeliveryNegativoDebeResponder400()
            throws Exception {

        String json = """
                {
                    "customerName": "Cliente Test",
                    "requiresDelivery": true,
                    "address": "Av. Principal 123",
                    "district": "Chorrillos",
                    "deliveryFee": -1,
                    "source": "WEB"
                }
                """;

        mockMvc.perform(
                        post("/api/orders")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(json)
                )
                .andExpect(
                        status().isBadRequest()
                );

        verifyNoInteractions(
                orderService
        );
    }


    // ============================================================
    // MANUAL ITEM
    // ============================================================

    @Test
    void productoManualSinDescripcionDebeResponder400()
            throws Exception {

        String json = """
                {
                    "description": "",
                    "quantity": 2,
                    "unitPrice": 15.50,
                    "notes": "Prueba"
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/orders/1/manual-items"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(json)
                )
                .andExpect(
                        status().isBadRequest()
                );

        verifyNoInteractions(
                orderService
        );
    }


    @Test
    void productoManualConCantidadCeroDebeResponder400()
            throws Exception {

        String json = """
                {
                    "description": "Producto manual",
                    "quantity": 0,
                    "unitPrice": 15.50
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/orders/1/manual-items"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(json)
                )
                .andExpect(
                        status().isBadRequest()
                );

        verifyNoInteractions(
                orderService
        );
    }


    @Test
    void productoManualConPrecioNegativoDebeResponder400()
            throws Exception {

        String json = """
                {
                    "description": "Producto manual",
                    "quantity": 2,
                    "unitPrice": -10.00
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/orders/1/manual-items"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(json)
                )
                .andExpect(
                        status().isBadRequest()
                );

        verifyNoInteractions(
                orderService
        );
    }


    // ============================================================
    // CATALOG ITEM
    // ============================================================

    @Test
    void productoCatalogoConIdCeroDebeResponder400()
            throws Exception {

        String json = """
                {
                    "productPriceId": 0
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/orders/1/catalog-items"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(json)
                )
                .andExpect(
                        status().isBadRequest()
                );

        verifyNoInteractions(
                orderService
        );
    }


    // ============================================================
    // ORDER STATUS
    // ============================================================

    @Test
    void cambiarEstadoSinStatusDebeResponder400()
            throws Exception {

        String json = """
                {
                }
                """;

        mockMvc.perform(
                        patch(
                                "/api/orders/1/status"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(json)
                )
                .andExpect(
                        status().isBadRequest()
                );

        verifyNoInteractions(
                orderService
        );
    }


    @Test
    void cambiarEstadoConEnumInvalidoDebeResponder400()
            throws Exception {

        String json = """
                {
                    "status": "EN_CAMINO"
                }
                """;

        mockMvc.perform(
                        patch(
                                "/api/orders/1/status"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(json)
                )
                .andExpect(
                        status().isBadRequest()
                );

        verifyNoInteractions(
                orderService
        );
    }
}