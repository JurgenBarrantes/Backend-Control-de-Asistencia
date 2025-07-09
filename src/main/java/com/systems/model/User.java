package com.systems.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "users")
public class User { //Usuarios del sistema como profesores, administradores, etc.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer idUser;

    @Column(nullable = false, length = 60)
    private String username; //Nombre de usuario

    @Column(nullable = false)
    private String password; //Contraseña del usuario

    @Column(nullable = false)
    private Boolean enabled;

    @ManyToMany(fetch =  FetchType.EAGER)
    @JoinTable(name = "user_role", 
                joinColumns = @JoinColumn(name = "id_user", referencedColumnName = "idUser"),
                inverseJoinColumns = @JoinColumn(name = "id_role", referencedColumnName = "idRole"))
    private List<Role> roles; // Relación con la entidad Role, que representa el rol del usuario en el sistema
    
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Person person; // Relación inversa con Person
}
