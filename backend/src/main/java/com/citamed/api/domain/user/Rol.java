package com.citamed.api.domain.user;

/**
 * Define los roles de usuario permitidos en el sistema.
 * * Este Enum se mapeará a la columna 'rol' ENUM('ADMIN','PACIENTE')
 * de la tabla 'Usuarios' en la base de datos.
 */
public enum Rol {
    ADMIN,
    PACIENTE
}