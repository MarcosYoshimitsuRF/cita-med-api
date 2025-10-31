package com.citamed.api.domain.patient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repositorio para la entidad Paciente.
 * Define la capa de acceso a datos para la tabla 'Pacientes'.
 * Cumple con los Puntos 1.4.4 y 1.4.5 del roadmap.
 */
@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    /**
     * Llama al Procedimiento Almacenado transaccional para registrar
     * un nuevo paciente (insertando en 'Usuarios' y 'Pacientes').
     * Cumple con el Punto 1.4.5 del roadmap.
     */
    @Modifying
    @Transactional
    @Procedure(name = "sp_RegistrarPaciente")
    void registrarPaciente(
            @Param("p_email") String email,
            @Param("p_password_hash") String passwordHash,
            @Param("p_dni") String dni,
            @Param("p_nombres") String nombres,
            @Param("p_apellidos") String apellidos,
            @Param("p_telefono") String telefono
    );
}