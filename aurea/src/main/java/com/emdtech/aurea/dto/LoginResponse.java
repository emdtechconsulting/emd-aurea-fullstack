package com.emdtech.aurea.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "LoginResponse",
        description = "Respuesta devuelta después de una autenticación correcta"
)
public class LoginResponse {

    @Schema(example = "admin")
    private String username;

    @Schema(example = "ADMIN")
    private String role;

    @Schema(example = "Autenticación correcta")
    private String message;

    @Schema(
            description = "JWT utilizado para acceder a los endpoints protegidos"
    )
    private String token;

    @Schema(example = "Bearer")
    private String tokenType;

    @Schema(example = "3600")
    private long expiresIn;


    public LoginResponse() {
    }


    public LoginResponse(
            String username,
            String role,
            String message,
            String token,
            String tokenType,
            long expiresIn) {

        this.username = username;
        this.role = role;
        this.message = message;
        this.token = token;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }


    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
}