package com.systems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAttendanceDTO {
    private Integer studentId;// ID del estudiante
    private Boolean isPresent; // ¿Está presente?
    private Boolean isLate; // ¿Llegó tarde?
    private String observations; // Observaciones opcionales
}
