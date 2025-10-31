package com.citamed.api.auth.dtos;

/**
 * DTO para la respuesta de autenticación (Login).
 * Contiene el JWT que el frontend debe almacenar.
 * Cumple con el Punto 1.7.1 del roadmap.
 */
public record AuthResponse(String token) {
}