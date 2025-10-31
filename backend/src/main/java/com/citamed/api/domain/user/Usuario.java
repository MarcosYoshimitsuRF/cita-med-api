package com.citamed.api.domain.user;

import com.citamed.api.domain.patient.Paciente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Puntos 1.3.2, 1.3.3, 1.3.4, 1.3.8
 * Entidad que mapea la tabla 'Usuarios' y representa
 * un usuario autenticable para Spring Security.
 */
@Entity
@Table(name = "Usuarios")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idUsuario")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "email")
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol")
    private Rol rol;

    @Column(name = "esta_activo")
    private Boolean estaActivo;

    // Punto 1.3.8: Relación inversa 1:1 con Paciente
    @OneToOne(mappedBy = "usuario")
    private Paciente paciente;


    // --- Métodos de UserDetails (Punto 1.3.4) ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // El rol debe prefijarse con "ROLE_" para Spring Security
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    @Override
    public String getPassword() {
        return this.passwordHash; // Mapea password_hash
    }

    @Override
    public String getUsername() {
        return this.email; // Usamos email como username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.estaActivo; // Mapea esta_activo
    }
}