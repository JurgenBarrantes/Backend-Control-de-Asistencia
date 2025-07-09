package com.systems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceReportDTO {
    private Integer studentId;
    private String studentName;
    private String studentDni;
    private String subjectName;
    private String teacherName;

    // Estadísticas del período
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalClasses;
    private Integer presentCount;
    private Integer lateCount;
    private Integer absentCount;

    public AttendanceReportDTO(Integer studentId, String studentFirstName, String studentLastName, Long presentCount,
            Long lateCount, Long absentCount) {
        this.studentId = studentId;
        this.studentName = studentFirstName + " " + studentLastName;
        this.presentCount = presentCount.intValue();
        this.lateCount = lateCount.intValue();
        this.absentCount = absentCount.intValue();
    }

    // Porcentajes
    private Double attendancePercentage;
    private Double latePercentage;
    private Double absentPercentage;

    // Estado
    private String status; // "Good", "Warning", "Critical"
    private String observations;
}
