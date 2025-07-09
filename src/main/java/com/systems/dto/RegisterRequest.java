package com.systems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    // Datos para User
    private String username;
    private String email;
    private String password;
    
    // Datos para Person
    private String firstName;
    private String lastName;
    private String dni;
    private String phone;
    private String birthdate; // formato: yyyy-MM-dd
    private String gender; // M, F
    private String address;
    
    // Rol opcional (por defecto será "USER")
    private String roleName;
}
