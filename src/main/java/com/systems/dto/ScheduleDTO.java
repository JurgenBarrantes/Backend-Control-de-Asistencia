package com.systems.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduleDTO {
    private Integer idSchedule;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private ClassroomDTO classroom;
    private SubjectDTO subject;
}
