package com.citamed.api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Puntos 1.6.1 a 1.6.4
 * Filtro que se ejecuta una vez por cada petición.
 * Valida el token JWT y establece la autenticación en el contexto de seguridad.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    // Inyectaremos esto en el Paso 1.7
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. (Punto 1.6.2) Extraer el Header "Authorization"
        final String authHeader = request.getHeader("Authorization");

        // 2. Si no hay header o no empieza con "Bearer ", pasar al siguiente filtro
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraer el token (quitando "Bearer ")
        final String jwt = authHeader.substring(7);

        // 4. (Punto 1.6.3) Extraer el email (username) del token
        final String userEmail = jwtService.getUsernameFromToken(jwt);

        // 5. Validar el token y el contexto de seguridad
        // Si hay email y el usuario AÚN NO está autenticado en el contexto...
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Cargar el usuario desde la BD (usando el UserDetailsService)
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // Validar si el token corresponde a este usuario y no ha expirado
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // (Punto 1.6.4) Crear la autenticación y establecerla en el SecurityContextHolder
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // No se necesitan credenciales (password) en este punto
                        userDetails.getAuthorities()
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Establecer al usuario como autenticado
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 6. Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}