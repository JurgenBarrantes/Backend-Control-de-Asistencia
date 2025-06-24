package com.systems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDTO {
    private Integer idAttendance;
    private String date;
    private String entryTime;
    private boolean isPresent;
    private boolean isLate;
    
    // IDs para las relaciones
    private Integer classroomId;
    private Integer scheduleId;
    private Integer studentId;
}
