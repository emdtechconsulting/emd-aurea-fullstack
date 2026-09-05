package com.emdtech.aurea.exception;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc createMockMvc() {

        return MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldReturnControlled500() throws Exception {

        MockMvc mockMvc = createMockMvc();

        mockMvc.perform(
                        get("/api/test/error")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isInternalServerError())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error")
                        .value("Internal Server Error"))
                .andExpect(jsonPath("$.message")
                        .value("Ocurrió un error interno inesperado"))
                .andExpect(jsonPath("$.path")
                        .value("/api/test/error"));
    }

    @Test
    void shouldReturn404WhenResourceDoesNotExist() throws Exception {

        MockMvc mockMvc = createMockMvc();

        mockMvc.perform(
                        get("/api/test/not-found")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error")
                        .value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Recurso de prueba no encontrado"))
                .andExpect(jsonPath("$.path")
                        .value("/api/test/not-found"));
    }

    @Test
    void shouldReturn400WhenBusinessRuleFails() throws Exception {

        MockMvc mockMvc = createMockMvc();

        mockMvc.perform(
                        get("/api/test/business-rule")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error")
                        .value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value("Regla de negocio de prueba inválida"))
                .andExpect(jsonPath("$.path")
                        .value("/api/test/business-rule"));
    }

    @Test
    void shouldReturn400WithFieldErrorsWhenRequestIsInvalid() throws Exception {

        MockMvc mockMvc = createMockMvc();

        String json = """
                {
                    "customerName": "",
                    "deliveryFee": -10
                }
                """;

        mockMvc.perform(
                        post("/api/test/validation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error")
                        .value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value("Datos de entrada inválidos"))
                .andExpect(jsonPath("$.path")
                        .value("/api/test/validation"))
                .andExpect(jsonPath("$.fieldErrors.customerName")
                        .value("El nombre del cliente es obligatorio"))
                .andExpect(jsonPath("$.fieldErrors.deliveryFee")
                        .value("El costo de delivery no puede ser negativo"));
    }

    @RestController
    static class TestController {

        @GetMapping("/api/test/error")
        public String generateError() {

            throw new RuntimeException(
                    "Error interno generado solamente para la prueba"
            );
        }

        @GetMapping("/api/test/not-found")
        public String generateNotFound() {

            throw new ResourceNotFoundException(
                    "Recurso de prueba no encontrado"
            );
        }

        @GetMapping("/api/test/business-rule")
        public String generateBusinessRuleError() {

            throw new BusinessRuleException(
                    "Regla de negocio de prueba inválida"
            );
        }

        @PostMapping("/api/test/validation")
        public String validateRequest(
                @Valid @RequestBody ValidationRequest request) {

            return "OK";
        }
    }

    static class ValidationRequest {

        @NotBlank(
                message = "El nombre del cliente es obligatorio"
        )
        private String customerName;

        @PositiveOrZero(
                message = "El costo de delivery no puede ser negativo"
        )
        private BigDecimal deliveryFee;

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public BigDecimal getDeliveryFee() {
            return deliveryFee;
        }

        public void setDeliveryFee(BigDecimal deliveryFee) {
            this.deliveryFee = deliveryFee;
        }
    }
}