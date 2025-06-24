package com.systems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private Integer idStudent;
    
    // Información básica de la persona asociada
    private String firstName;
    private String lastName;
    private String fullName; // Nombre completo concatenado
    private String dni;
    private String email;
    private String phone;
}
