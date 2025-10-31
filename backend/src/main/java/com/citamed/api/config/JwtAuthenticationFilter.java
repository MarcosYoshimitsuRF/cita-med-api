package com.citamed.api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que intercepta cada petición para validar el JWT.
 * Cumple con los Puntos 1.6.1 al 1.6.4 del roadmap.
 * Se ejecuta una vez por cada petición (OncePerRequestFilter).
 */
@Component
@RequiredArgsConstructor // (Lombok) Genera un constructor con los campos 'final'
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Extraer el Header 'Authorization' (Punto 1.6.2)
        final String authHeader = request.getHeader("Authorization");

        // 2. Validar si el Header es nulo o no es 'Bearer'
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Continuar al siguiente filtro
            return;
        }

        // 3. Extraer el token (Punto 1.6.2)
        final String jwt = authHeader.substring(7); // "Bearer ".length()

        // 4. Extraer el username (email) del token (Punto 1.6.3)
        final String userEmail;
        try {
            userEmail = jwtService.getUsernameFromToken(jwt);
        } catch (Exception e) {
            // Si el token es inválido (expirado, malformado), no autenticamos
            filterChain.doFilter(request, response);
            return;
        }

        // 5. Validar el token y el contexto de seguridad (Punto 1.6.3)
        // Si el email no es nulo Y el usuario aún no está autenticado en el contexto
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Cargar el UserDetails desde la BD (usando nuestro UserDetailsService)
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // Validar el token contra los UserDetails
            if (jwtService.validateToken(jwt, userDetails)) {

                // 6. Si el token es válido, establecer el contexto (Punto 1.6.4)
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // Credenciales (password) son nulas, ya está validado
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Establecer la autenticación en el SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continuar al siguiente filtro en la cadena
        filterChain.doFilter(request, response);
    }
}