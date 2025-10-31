package com.citamed.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService; // NUEVA IMPORTACIÓN
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    // Inyectamos nuestro UserDetailsService personalizado
    private final UserDetailsService userDetailsService; // NUEVA INYECCIÓN

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Punto 1.7.8: Exponer el Bean de UserDetailsService.
     * * NOTA: Aunque ya inyectamos 'userDetailsService' (porque tiene @Service),
     * este Bean es explícito para claridad. Spring Boot lo detectaría
     * automáticamente, pero el roadmap lo pide explícitamente.
     * En este caso, simplemente retornamos la instancia ya inyectada.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return userDetailsService;
    }
}