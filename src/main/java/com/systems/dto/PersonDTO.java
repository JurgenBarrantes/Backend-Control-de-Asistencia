package com.systems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private Integer userId; // ID del usuario asociado (opcional)
}
