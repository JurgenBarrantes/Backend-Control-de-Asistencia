package com.systems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssistDTO {
    
    private Integer idAssist;
    
    private String status;
    
    // IDs de las entidades relacionadas
    private Integer attendanceId;
    private Integer enrollmentId;
    
    // Información adicional de la asistencia (para consultas)
    private String attendanceDate;
    private String attendanceEntryTime;
    private Boolean attendancePresent;
    private Boolean attendanceLate;
    
    // Información adicional del enrollment (para consultas)
    private Integer studentId;
    private String studentFirstName;
    private String studentLastName;
    private String studentFullName;
    private Integer classroomId;
    private String classroomName;
    
}
