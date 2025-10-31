package com.citamed.api.config;

import com.citamed.api.domain.user.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Puntos 1.5.1 a 1.5.5
 * Servicio para manejar la creación, validación y extracción de JWTs.
 */
@Service
public class JwtService {

    // Punto 1.5.5: Inyecta la clave secreta desde application.properties
    @Value("${jwt.secret.key}")
    private String jwtSecret;

    // Define el tiempo de expiración del token (ej. 24 horas)
    private static final long JWT_EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    /**
     * Punto 1.5.4: Extrae el username (email) del token.
     */
    public String getUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Punto 1.5.2: Genera un token para un UserDetails (Usuario).
     */
    public String generateToken(UserDetails userDetails) {
        // Hacemos cast a nuestra entidad Usuario para acceder a campos custom
        Usuario usuario = (Usuario) userDetails;

        // Creamos los "claims" (datos) personalizados
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("id_usuario", usuario.getIdUsuario());
        extraClaims.put("rol", usuario.getRol().name());
        extraClaims.put("full_name", usuario.getPaciente() != null ?
                usuario.getPaciente().getNombres() + " " + usuario.getPaciente().getApellidos() :
                "Administrador");

        return buildToken(extraClaims, userDetails, JWT_EXPIRATION_TIME);
    }

    /**
     * Punto 1.5.3: Valida el token contra el UserDetails.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // --- Métodos Helper Privados ---

    /**
     * Construye el token JWT final.
     */
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername()) // Username (email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Método genérico para extraer cualquier "claim" (dato) del token.
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parsea el token y extrae todos los claims usando la clave secreta.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Obtiene la clave de firma (SecretKey) a partir del string base64.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}