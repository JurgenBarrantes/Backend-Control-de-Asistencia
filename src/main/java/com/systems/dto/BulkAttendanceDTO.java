package com.systems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkAttendanceDTO {
    private String date;           // Fecha común para todas las asistencias
    private String entryTime;      // Hora común para todas las asistencias
    private Integer classroomId;   // Aula común
    private Integer scheduleId;    // Horario común
    private List<StudentAttendanceDTO> students; // Lista de estudiantes con su asistencia
}
