package com.citamed.api.config;

import com.citamed.api.domain.user.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración central de Beans.
 * ARCHIVO ACTUALIZADO: Se añade el UserDetailsService (1.7.8) y el
 * AuthenticationProvider necesario para que SecurityConfig funcione.
 */
@Configuration
public class AppConfig {

    /**
     * Define el Bean para encriptar y verificar contraseñas.
     * (Punto 1.2.1)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Expone el AuthenticationManager de Spring Security como un Bean.
     * (Punto 1.2.2)
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Expone nuestro ApplicationUserDetailsService como el Bean principal.
     * Cumple con el Punto 1.7.8 del roadmap.
     * *
     * Nota: Spring inyectará automáticamente la implementación
     * 'ApplicationUserDetailsService' que acabamos de crear.
     */
    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
        return new ApplicationUserDetailsService(usuarioRepository);
    }

    /**
     * Define el Proveedor de Autenticación.
     * Este es el componente que "une" el UserDetailsService (que busca usuarios)
     * con el PasswordEncoder (que verifica contraseñas).
     */
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
}