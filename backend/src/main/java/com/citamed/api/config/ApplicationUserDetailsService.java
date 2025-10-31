package com.citamed.api.config;

import com.citamed.api.domain.user.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementación de UserDetailsService (Punto 1.7.7).
 * Conecta Spring Security con nuestro UsuarioRepository para
 * cargar los detalles del usuario durante el login y la validación del token.
 */
@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public ApplicationUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Carga un usuario (en nuestro caso, por email) desde la BD.
     * Utiliza el SP 'sp_ObtenerUsuarioPorEmail' a través del repositorio.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Usamos el método que llama a nuestro SP de login (Punto 1.4.2)
        return usuarioRepository.findByEmailParaLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado o no activo: " + username));
    }
}