package com.citamed.api.config;

import com.citamed.api.domain.user.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Punto 1.7.7: Implementa UserDetailsService.
 * Carga el usuario desde la BD usando el SP sp_ObtenerUsuarioPorEmail.
 */
@Service
@RequiredArgsConstructor
public class ApplicationUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 'username' es el email en nuestro sistema
        return usuarioRepository.findByEmailParaLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado o inactivo: " + username));
    }
}