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
 * Controlador REST para exponer los endpoints de Autenticación.
 * Cumple con el Paso 1.8 del roadmap.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint para el inicio de sesión (Login).
     * Cumple con el Punto 1.8.2.
     * Mapeado a POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request
    ) {
        // Delega la lógica de negocio al AuthService
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para el registro de nuevos pacientes.
     * Cumple con el Punto 1.8.3.
     * Mapeado a POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @RequestBody RegisterRequest request
    ) {
        // Delega la lógica de negocio al AuthService
        authService.register(request);
        return ResponseEntity.ok().build(); // Devuelve 200 OK
    }
}