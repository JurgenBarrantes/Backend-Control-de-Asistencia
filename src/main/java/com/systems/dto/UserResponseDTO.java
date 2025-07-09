package com.systems.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Integer idUser;
    private String username;
    // NO incluir password por seguridad
    private Boolean enabled;
    private List<RoleDTO> roles;
    // NO incluir person para evitar recursión infinita
}
