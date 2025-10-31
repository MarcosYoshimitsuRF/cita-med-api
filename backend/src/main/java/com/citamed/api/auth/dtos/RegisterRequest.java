package com.citamed.api.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String dni;
    private String nombres;
    private String apellidos;
    private String email;
    private String password;
    private String telefono;
}