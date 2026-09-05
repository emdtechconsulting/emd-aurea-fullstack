package com.emdtech.aurea.controller;

import com.emdtech.aurea.exception.GlobalExceptionHandler;
import com.emdtech.aurea.order.Order;
import com.emdtech.aurea.service.OrderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class OrderControllerPaginationTest {

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


    @Test
    void pageNegativoDebeResponder400()
            throws Exception {

        mockMvc.perform(
                        get("/api/orders")
                                .param(
                                        "page",
                                        "-1"
                                )
                                .param(
                                        "size",
                                        "10"
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );

        verify(
                orderService,
                never()
        ).listarPedidosPaginados(
                any(),
                any(),
                any(),
                any()
        );
    }


    @Test
    void sizeCeroDebeResponder400()
            throws Exception {

        mockMvc.perform(
                        get("/api/orders")
                                .param(
                                        "page",
                                        "0"
                                )
                                .param(
                                        "size",
                                        "0"
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );

        verify(
                orderService,
                never()
        ).listarPedidosPaginados(
                any(),
                any(),
                any(),
                any()
        );
    }


    @Test
    void sizeMayorACienDebeResponder400()
            throws Exception {

        mockMvc.perform(
                        get("/api/orders")
                                .param(
                                        "page",
                                        "0"
                                )
                                .param(
                                        "size",
                                        "101"
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );

        verify(
                orderService,
                never()
        ).listarPedidosPaginados(
                any(),
                any(),
                any(),
                any()
        );
    }


    @Test
    void pageCeroSizeDiezDebeSerValido()
            throws Exception {

        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(
                        Pageable.class
                );

        Page<Order> emptyPage =
                new PageImpl<>(
                        List.of()
                );

        when(
                orderService.listarPedidosPaginados(
                        isNull(),
                        isNull(),
                        isNull(),
                        any(Pageable.class)
                )
        ).thenReturn(
                emptyPage
        );

        mockMvc.perform(
                        get("/api/orders")
                                .param(
                                        "page",
                                        "0"
                                )
                                .param(
                                        "size",
                                        "10"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.content")
                                .isArray()
                );

        verify(
                orderService
        ).listarPedidosPaginados(
                isNull(),
                isNull(),
                isNull(),
                pageableCaptor.capture()
        );

        Pageable pageable =
                pageableCaptor.getValue();

        assertEquals(
                0,
                pageable.getPageNumber()
        );

        assertEquals(
                10,
                pageable.getPageSize()
        );

        assertTrue(
                pageable
                        .getSort()
                        .getOrderFor("id")
                        .isDescending()
        );
    }
}