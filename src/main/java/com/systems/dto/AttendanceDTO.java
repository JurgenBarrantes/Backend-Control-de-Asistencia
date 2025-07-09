package com.systems.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttendanceDTO {
    private Integer idAttendance;
    private String date;
    private String entryTime;

    @JsonProperty("present")
    private boolean isPresent;

    @JsonProperty("late")
    private boolean isLate;

    // Objetos completos para las relaciones
    private ClassroomDTO classroom;
    private SimpleScheduleDTO schedule;
    private StudentDTO student;

    // IDs para compatibilidad con operaciones de creación/actualización
    private Integer classroomId;
    private Integer scheduleId;
    private Integer studentId;
}
