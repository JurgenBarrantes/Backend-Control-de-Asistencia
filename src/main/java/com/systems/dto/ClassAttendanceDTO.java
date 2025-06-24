package com.systems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassAttendanceDTO {
    // Información común de la clase
    private String date;              // "2025-06-24"
    private String entryTime;         // "08:30:00"
    private Integer classroomId;      // ID del aula
    private Integer scheduleId;       // ID del horario
    private Integer subjectId;        // ID de la materia
    private Integer teacherId;        // ID del profesor
    
    // Lista de estudiantes con su asistencia individual
    private List<StudentAttendanceDTO> studentsAttendance;
}
