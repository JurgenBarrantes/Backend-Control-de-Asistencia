package com.systems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassAttendanceResponseDTO {
    private String date;
    private String className;
    private String subjectName;
    private String teacherName;
    private Integer totalStudents;
    private Integer presentCount;
    private Integer lateCount;
    private Integer absentCount;
    private List<AttendanceDTO> attendances; // Lista de asistencias creadas
}
