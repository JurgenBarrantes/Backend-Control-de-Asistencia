package com.systems.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "roles")
public class Role { // Roles del sistema como profesores, administradores, etc.
    @Id
    // TEMPORAL: @GeneratedValue comentado hasta que se configure AUTO_INCREMENT en la DB
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer idRole;

    @Column(nullable = false, length = 60)
    private String name; // Nombre del rol, por ejemplo: "Profesor", "Administrador", etc.

    @Column(nullable = false, length = 50)
    private String description; // Descripción del rol, por ejemplo: "Profesor de asistencia", "Administrador del sistema", etc.

    // Constructor para crear un rol sin ID (para POST)
    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
