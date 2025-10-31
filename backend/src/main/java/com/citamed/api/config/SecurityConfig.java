package com.citamed.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración central de Spring Security (el "Firewall").
 * ARCHIVO ACTUALIZADO: Se inyecta el AuthenticationProvider (1.7.9)
 * y se añade el JwtAuthenticationFilter (1.6.5).
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // Inyección de nuestro filtro y proveedor
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    /**
     * Define la cadena de filtros de seguridad que se aplicará a todas
     * las peticiones HTTP que ingresen a la aplicación.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // (Punto 1.2.4) Deshabilitar CSRF
        http.csrf(AbstractHttpConfigurer::disable);

        // (Puntos 1.2.6 y 1.2.7) Autorización de Peticiones
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/login", "/auth/register").permitAll()
                .anyRequest().authenticated()
        );

        // (Punto 1.2.5) Configurar la sesión como STATELESS
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // (Punto 1.7.9) Indicar a Spring que use nuestro AuthenticationProvider
        http.authenticationProvider(authenticationProvider);

        // (Punto 1.6.5) Añadir nuestro filtro JWT ANTES del filtro estándar
        // de username/password.
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}