package com.systems.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClassroomDTO {
    private Integer idClassroom;
    private String name;

    // DTOs completos
    private TeacherDTO teacher;
    private SubjectDTO subject;
    private List<SimpleScheduleDTO> schedules;
}
