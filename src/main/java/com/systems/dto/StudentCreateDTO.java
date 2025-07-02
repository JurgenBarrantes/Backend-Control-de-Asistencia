package com.systems.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCreateDTO {
    
    @NotNull(message = "Person ID is required")
    private Integer personId;
    
    @Size(max = 20, message = "Student code must not exceed 20 characters")
    private String studentCode;
    
    @Size(max = 100, message = "Program must not exceed 100 characters")
    private String program;
    
    private Integer semester;
    private String academicYear;
}
