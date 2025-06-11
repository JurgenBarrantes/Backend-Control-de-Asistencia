package com.systems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTO {
    private Integer idSchedule;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private String classroodayOfWeekm;
    private String subject;
}
