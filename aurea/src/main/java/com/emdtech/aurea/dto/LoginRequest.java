package com.emdtech.aurea.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;


@Schema(
        name = "LoginRequest",
        description = "Credenciales utilizadas para iniciar sesión en AUREA"
)
public class LoginRequest {

    @Schema(
            description = "Nombre de usuario",
            example = "admin"
    )
    @NotBlank(
            message = "El usuario es obligatorio"
    )
    private String username;


    @Schema(
            description = "Contraseña del usuario",
            example = "********"
    )
    @NotBlank(
            message = "La contraseña es obligatoria"
    )
    private String password;


    public String getUsername() {
        return username;
    }


    public void setUsername(
            String username) {

        this.username = username;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(
            String password) {

        this.password = password;
    }
}