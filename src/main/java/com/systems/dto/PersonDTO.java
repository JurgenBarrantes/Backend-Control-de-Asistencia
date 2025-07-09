package com.systems.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonDTO {
    private Integer idPerson;
    private String dni;
    private String firstName;
    private String lastName;
    private String birthdate;
    private String gender;
    private String address;
    private String phone;
    private String email;
    private UserResponseDTO user; // Objeto User sin password por seguridad
}
