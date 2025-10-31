package com.citamed.api.config;

import com.citamed.api.domain.user.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio para manejar la lógica de JSON Web Tokens (JWT).
 * Cumple con el Paso 1.5 (Puntos 1.5.1 al 1.5.5).
 * Responsable de la creación y validación de tokens.
 */
@Service
public class JwtService {

    // 1. Inyección de la clave secreta (Punto 1.5.5)
    @Value("${app.jwt.secret}")
    private String JWT_SECRET;

    // 2. Tiempo de expiración del token (ej. 24 horas)
    private static final long JWT_EXPIRATION = 1000 * 60 * 60 * 24;

    /**
     * Extrae el 'username' (email) del token.
     * Cumple con el Punto 1.5.4.
     */
    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    /**
     * Valida si un token es correcto y corresponde al usuario.
     * Cumple con el Punto 1.5.3.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Genera un nuevo token para un usuario.
     * Cumple con el Punto 1.5.2.
     */
    public String generateToken(UserDetails userDetails) {
        // Debemos castear UserDetails a nuestra entidad 'Usuario'
        // para acceder a los campos personalizados 'idUsuario' y 'rol'.
        Usuario usuario = (Usuario) userDetails;

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("rol", usuario.getRol().name());
        extraClaims.put("id_usuario", usuario.getIdUsuario());

        return buildToken(extraClaims, userDetails, JWT_EXPIRATION);
    }


    // --- Métodos privados de utilidad ---

    /**
     * Construye el token con los claims personalizados.
     */
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Verifica si el token ha expirado.
     */
    private boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }

    /**
     * Obtiene la fecha de expiración del token.
     */
    private Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    /**
     * Función genérica para extraer un 'claim' (dato) del token.
     */
    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parsea el token y extrae todos los 'claims' usando la clave secreta.
     */
    private Claims getAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Prepara la clave secreta (Base64) para que la librería JJWT la use.
     * Cumple con el Punto 1.5.5.
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}