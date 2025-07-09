package com.systems.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private TokenInfo tokens;

    // Campos del usuario sin encapsular
    private Integer idUser;
    private String username;
    private Boolean enabled;
    private List<String> roles;
    private PersonDTO person;

    public LoginResponse(TokenInfo tokens, AuthUserDTO user) {
        this.tokens = tokens;
        this.idUser = user.getIdUser();
        this.username = user.getUsername();
        this.enabled = user.getEnabled();
        this.roles = user.getRoles();
        this.person = user.getPerson();
    }
}
