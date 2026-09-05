package com.emdtech.aurea.config;

import io.swagger.v3.oas.models.OpenAPI;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Configuration
@Profile("!prod")
public class OpenApiConfig {

    @Bean
    public OpenAPI aureaOpenAPI() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title(
                                        "AUREA API"
                                )
                                .description(
                                        "API REST para la gestión de pedidos, " +
                                        "productos, precios, estados y operaciones " +
                                        "del sistema AUREA."
                                )
                                .version(
                                        "1.0.0"
                                )
                                .contact(
                                        new Contact()
                                                .name(
                                                        "EMD Tech Consulting"
                                                )
                                )
                );
    }
}