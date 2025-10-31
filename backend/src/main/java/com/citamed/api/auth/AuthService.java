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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PacienteRepository pacienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Punto 1.7.5: Lógica de Login
     */
    public AuthResponse login(LoginRequest request) {
        // 1. Spring AuthenticationManager valida email y password
        // (Usa internamente UserDetailsService y PasswordEncoder)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. Si la autenticación es exitosa, buscamos al usuario
        // Usamos findByEmail (el SP) para obtener el objeto Usuario completo
        var usuario = usuarioRepository.findByEmailParaLogin(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado después de autenticación"));

        // 3. Generamos el token JWT
        var jwtToken = jwtService.generateToken(usuario);

        // 4. Devolvemos el token
        return AuthResponse.builder().token(jwtToken).build();
    }

    /**
     * Punto 1.7.6: Lógica de Registro
     */
    public void register(RegisterRequest request) {
        // 1. Validar si el email ya existe
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            // No usamos una excepción custom por simplicidad,
            // pero en producción se usaría una excepción de negocio.
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // 2. Hashear la contraseña
        String passwordHashed = passwordEncoder.encode(request.getPassword());

        // 3. Llamar al SP sp_RegistrarPaciente
        // El SP maneja la transacción de insertar en Usuarios y Pacientes
        pacienteRepository.registrarPaciente(
                request.getEmail(),
                passwordHashed,
                request.getDni(),
                request.getNombres(),
                request.getApellidos(),
                request.getTelefono()
        );

        // 4. No devolvemos nada (HTTP 200 OK implícito)
    }
}