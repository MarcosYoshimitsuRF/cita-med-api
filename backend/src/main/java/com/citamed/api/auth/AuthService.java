package com.citamed.api.auth;

import com.citamed.api.auth.dtos.AuthResponse;
import com.citamed.api.auth.dtos.LoginRequest;
import com.citamed.api.auth.dtos.RegisterRequest;
import com.citamed.api.config.JwtService;
import com.citamed.api.domain.patient.PacienteRepository;
import com.citamed.api.domain.user.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio que centraliza la lógica de negocio de autenticación.
 * Cumple con los Puntos 1.7.4, 1.7.5 y 1.7.6 del roadmap.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    // Inyección de todas las dependencias necesarias
    private final UsuarioRepository usuarioRepository;
    private final PacienteRepository pacienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Lógica de Negocio para el Login (Punto 1.7.5)
     */
    public AuthResponse login(LoginRequest request) {
        // 1. Autenticar usando el manager de Spring
        // Esto valida el email y password usando nuestro UserDetailsService
        // y PasswordEncoder
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        // 2. Si la autenticación fue exitosa, buscar al usuario (UserDetails)
        // Usamos el SP de login para asegurarnos de que es el usuario correcto
        UserDetails user = usuarioRepository.findByEmailParaLogin(request.email())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado después de autenticación")); // Inesperado

        // 3. Generar y devolver el token
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    /**
     * Lógica de Negocio para el Registro (Punto 1.7.6)
     */
    @Transactional // Asegura que la operación sea atómica
    public void register(RegisterRequest request) {
        // 1. Validar email duplicado
        if (usuarioRepository.findByEmail(request.email()).isPresent()) {
            // Idealmente, se lanzaría una excepción personalizada
            throw new IllegalStateException("El correo electrónico ya está en uso");
        }

        // 2. Hashear la contraseña
        String hashedPassword = passwordEncoder.encode(request.password());

        // 3. Llamar al Procedimiento Almacenado de registro
        // (El SP maneja la transacción de 'Usuarios' y 'Pacientes')
        pacienteRepository.registrarPaciente(
                request.email(),
                hashedPassword,
                request.dni(),
                request.nombres(),
                request.apellidos(),
                request.telefono()
        );
    }
}