package com.systems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAttendanceDTO {
    private Integer studentId;
    private Boolean isPresent;
    private Boolean isLate;
}
