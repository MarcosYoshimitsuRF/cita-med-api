package com.citamed.api.auth.dtos;

public record RegisterRequest(
        String dni,
        String nombres,
        String apellidos,
        String email,
        String password,
        String telefono
) {
}