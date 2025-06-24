package com.systems.service;

import com.systems.dto.ClassAttendanceDTO;
import com.systems.dto.ClassAttendanceResponseDTO;
import com.systems.model.Attendance;

public interface IAttendanceService extends IGenericService<Attendance, Integer> {
    ClassAttendanceResponseDTO saveClassAttendance(ClassAttendanceDTO classAttendanceDto) throws Exception;
}
