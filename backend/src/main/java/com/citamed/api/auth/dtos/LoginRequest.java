package com.citamed.api.auth.dtos;

/**
 * DTO para la petición de Login.
 * Define la estructura del JSON que espera el endpoint /auth/login.
 * Cumple con el Punto 1.7.2 del roadmap.
 */
public record LoginRequest(String email, String password) {
}