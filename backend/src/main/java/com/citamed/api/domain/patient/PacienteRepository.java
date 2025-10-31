package com.citamed.api.domain.patient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Puntos 1.4.4, 1.4.5
 * Repositorio para la entidad Paciente.
 */
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    /**
     * Punto 1.4.5: Mapea el SP sp_RegistrarPaciente.
     * @Procedure indica el nombre del SP.
     * @Modifying indica que es una operación de escritura (INSERT).
     * @Transactional asegura que la transacción del SP se maneje correctamente.
     */
    @Modifying
    @Transactional
    @Procedure(name = "sp_RegistrarPaciente")
    void registrarPaciente(
            @Param("p_email") String p_email,
            @Param("p_password_hash") String p_password_hash,
            @Param("p_dni") String p_dni,
            @Param("p_nombres") String p_nombres,
            @Param("p_apellidos") String p_apellidos,
            @Param("p_telefono") String p_telefono
    );
}