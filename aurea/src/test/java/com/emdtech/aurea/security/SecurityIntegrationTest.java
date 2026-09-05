package com.emdtech.aurea.security;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtEncoder jwtEncoder;


    @Test
    void getOrdersSinTokenDebeResponder401()
            throws Exception {

        mockMvc.perform(
                get("/api/orders")
        )
        .andExpect(
                status().isUnauthorized()
        );
    }


    @Test
    void deleteItemConVentasDebeResponder403()
            throws Exception {

        String token =
                generarToken(
                        "ventas",
                        "VENTAS"
                );

        mockMvc.perform(
                delete(
                        "/api/orders/999999/items/999999"
                )
                .header(
                        "Authorization",
                        "Bearer " + token
                )
        )
        .andExpect(
                status().isForbidden()
        );
    }


    @Test
    void deleteItemConAdminDebePasarSeguridadYResponder404()
            throws Exception {

        String token =
                generarToken(
                        "admin",
                        "ADMIN"
                );

        mockMvc.perform(
                delete(
                        "/api/orders/999999/items/999999"
                )
                .header(
                        "Authorization",
                        "Bearer " + token
                )
        )
        .andExpect(
                status().isNotFound()
        );
    }


    @Test
    void tokenInvalidoDebeResponder401()
            throws Exception {

        mockMvc.perform(
                get("/api/orders")
                .header(
                        "Authorization",
                        "Bearer TOKEN_INVALIDO"
                )
        )
        .andExpect(
                status().isUnauthorized()
        );
    }


    private String generarToken(
            String username,
            String role) {

        Instant now =
                Instant.now();

        JwtClaimsSet claims =
                JwtClaimsSet.builder()
                        .subject(username)
                        .issuedAt(now)
                        .expiresAt(
                                now.plusSeconds(3600)
                        )
                        .claim(
                                "role",
                                role
                        )
                        .build();

        return jwtEncoder
                .encode(
                        JwtEncoderParameters
                                .from(claims)
                )
                .getTokenValue();
    }
}