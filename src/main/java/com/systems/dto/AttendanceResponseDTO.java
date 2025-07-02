package com.systems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponseDTO {
    private Integer idAttendance;
    private LocalDate date;
    private LocalTime entryTime;
    private Boolean isPresent;
    private Boolean isLate;
    
    // Información del estudiante
    private Integer studentId;
    private String studentName;
    private String studentDni;
    
    // Información del aula y horario
    private Integer classroomId;
    private String classroomName;
    private Integer scheduleId;
    private String subjectName;
    private String teacherName;
    
    // Información calculada
    private String status; // "Present", "Late", "Absent"
    private Integer minutesLate;
}
