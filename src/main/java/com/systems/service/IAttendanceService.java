package com.systems.service;

import com.systems.dto.ClassAttendanceDTO;
import com.systems.dto.ClassAttendanceResponseDTO;
import com.systems.dto.AttendanceReportDTO;
import com.systems.dto.BulkAttendanceDTO;
import com.systems.model.Attendance;

import java.util.List;

public interface IAttendanceService extends IGenericService<Attendance, Integer> {
    ClassAttendanceResponseDTO saveClassAttendance(ClassAttendanceDTO classAttendanceDto) throws Exception;

    void saveBulk(BulkAttendanceDTO bulkAttendanceDTO) throws Exception;

    List<ClassAttendanceResponseDTO> getAttendanceByClass(Integer classroomId);

    List<AttendanceReportDTO> getAttendanceReport(Integer classroomId, String startDate, String endDate);
}
