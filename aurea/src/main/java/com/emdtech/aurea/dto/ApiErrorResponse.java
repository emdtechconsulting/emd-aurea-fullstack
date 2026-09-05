package com.emdtech.aurea.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(
        name = "ApiErrorResponse",
        description = "Formato estándar utilizado por AUREA para devolver errores de la API"
)
public class ApiErrorResponse {

    @Schema(
            description = "Fecha y hora en que ocurrió el error",
            example = "2026-08-25T10:30:45"
    )
    private LocalDateTime timestamp;

    @Schema(
            description = "Código HTTP del error",
            example = "400"
    )
    private int status;

    @Schema(
            description = "Nombre estándar del error HTTP",
            example = "Bad Request"
    )
    private String error;

    @Schema(
            description = "Descripción del problema",
            example = "El tamaño de página debe estar entre 1 y 100"
    )
    private String message;

    @Schema(
            description = "Ruta de la API donde ocurrió el error",
            example = "/api/orders"
    )
    private String path;

    @Schema(
            description = "Errores específicos de validación asociados a campos del formulario",
            nullable = true
    )
    private Map<String, String> fieldErrors;

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
}