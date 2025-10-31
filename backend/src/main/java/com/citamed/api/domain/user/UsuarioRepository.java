package com.citamed.api.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Usuario.
 * Define la capa de acceso a datos para la tabla 'Usuarios'.
 * Cumple con los Puntos 1.4.1, 1.4.2 y 1.4.3 del roadmap.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Método de consulta (Query Method) para validación de registro.
     * Spring Data JPA genera automáticamente la consulta SQL (SELECT * ...)
     * basándose en el nombre del método.
     * Cumple con el Punto 1.4.3 del roadmap.
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Llama al Procedimiento Almacenado para el login (Autenticación).
     * Este método será usado por nuestro UserDetailsService.
     * Cumple con el Punto 1.4.2 del roadmap.
     * * @param p_email El email del usuario que intenta iniciar sesión.
     * @return Un Optional que contiene al Usuario si está activo y existe.
     */
    @Query(value = "CALL sp_ObtenerUsuarioPorEmail(:p_email)", nativeQuery = true)
    Optional<Usuario> findByEmailParaLogin(@Param("p_email") String p_email);

}