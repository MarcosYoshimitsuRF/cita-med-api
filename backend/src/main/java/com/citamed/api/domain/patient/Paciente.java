package com.citamed.api.domain.patient;

import com.citamed.api.domain.user.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa la tabla 'Pacientes'.
 * Define la relación Uno-a-Uno (dueña) con Usuario
 */
@Entity
@Table(name = "Pacientes")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idPaciente")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paciente")
    private Long idPaciente;

    @Column(unique = true)
    private String dni;

    private String nombres;

    private String apellidos;

    private String telefono;

    @Column(name = "esta_activo")
    private Boolean estaActivo;

    @OneToOne
    @JoinColumn(name = "id_usuario", unique = true, nullable = false)
    private Usuario usuario;
}