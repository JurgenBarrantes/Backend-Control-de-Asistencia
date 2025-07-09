package com.systems.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer idUser;
    private String username;
    private String password;
    private Boolean enabled; // Campo obligatorio en la base de datos
    private List<RoleDTO> roles; // Relación con roles
    private PersonDTO person; // Relación con persona (opcional)
}
