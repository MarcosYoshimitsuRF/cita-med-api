package com.citamed.api.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Puntos 1.4.1, 1.4.2, 1.4.3
 * Repositorio para la entidad Usuario.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Punto 1.4.2: Mapea el SP sp_ObtenerUsuarioPorEmail.
     * Se usará para el login (implementando UserDetailsService).
     * Usamos @Query nativa para llamar al SP.
     */
    @Query(value = "CALL sp_ObtenerUsuarioPorEmail(:p_email)", nativeQuery = true)
    Optional<Usuario> findByEmailParaLogin(@Param("p_email") String p_email);

    /**
     * Punto 1.4.3: Método estándar de Spring Data JPA.
     * Se usará para validar si un email ya existe durante el registro.
     */
    Optional<Usuario> findByEmail(String email);
}