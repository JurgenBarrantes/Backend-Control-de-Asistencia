package com.systems.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserDTO {
    private Integer idUser;
    private String username;
    private Boolean enabled;
    private List<String> roles; // Lista de strings con los nombres de los roles
    private PersonDTO person; // Relación con persona (opcional)
}
