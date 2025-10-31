package com.citamed.api.auth;

import com.citamed.api.auth.dtos.AuthResponse;
import com.citamed.api.auth.dtos.LoginRequest;
import com.citamed.api.auth.dtos.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Puntos 1.8.1, 1.8.2, 1.8.3 (Corregido)
 * Controlador REST para los endpoints de Autenticación.
 * No se usa @RequestMapping a nivel de clase para endpoints limpios.
 */
@RestController
@RequestMapping("/") // Se mapea a la raíz del context-path (/api)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Punto 1.8.2: Endpoint para Login
     * Ruta completa: POST /api/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Punto 1.8.3: Endpoint para Registro de Pacientes
     * Ruta completa: POST /api/register
     */
    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @RequestBody RegisterRequest request
    ) {
        authService.register(request);
        return ResponseEntity.ok().build();
    }
}