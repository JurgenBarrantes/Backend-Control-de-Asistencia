package com.systems.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.systems.dto.AttendanceDTO;
import com.systems.dto.ClassAttendanceDTO;
import com.systems.dto.ClassAttendanceResponseDTO;
import com.systems.dto.StudentAttendanceDTO;
import com.systems.model.Attendance;
import com.systems.repo.IAttendanceRepo;
import com.systems.repo.IGenericRepo;
import com.systems.service.IAttendanceService;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService extends GenericService<Attendance, Integer> implements IAttendanceService {
    private final IAttendanceRepo repo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Attendance, Integer> getRepo() {
        return repo;
    }

    @Override
    @Transactional
    public ClassAttendanceResponseDTO saveClassAttendance(ClassAttendanceDTO classDto) throws Exception {

        List<AttendanceDTO> createdAttendances = new ArrayList<>();
        int presentCount = 0;
        int lateCount = 0;
        int absentCount = 0;

        // Procesar cada estudiante individualmente
        for (StudentAttendanceDTO studentAtt : classDto.getStudentsAttendance()) {

            // Crear AttendanceDTO individual usando datos comunes + datos del estudiante
            AttendanceDTO attendanceDto = new AttendanceDTO();
            attendanceDto.setDate(classDto.getDate());
            attendanceDto.setEntryTime(classDto.getEntryTime());
            attendanceDto.setPresent(studentAtt.getIsPresent() != null ? studentAtt.getIsPresent() : false);
            attendanceDto.setLate(studentAtt.getIsLate() != null ? studentAtt.getIsLate() : false);
            attendanceDto.setClassroomId(classDto.getClassroomId());
            attendanceDto.setScheduleId(classDto.getScheduleId());
            attendanceDto.setStudentId(studentAtt.getStudentId());

            // REUTILIZAR LA LÓGICA EXISTENTE
            Attendance savedAttendance = save(convertToEntity(attendanceDto));
            createdAttendances.add(convertToDto(savedAttendance));

            // Contar estadísticas
            boolean present = studentAtt.getIsPresent() != null ? studentAtt.getIsPresent() : false;
            boolean late = studentAtt.getIsLate() != null ? studentAtt.getIsLate() : false;

            if (present) {
                presentCount++;
                if (late) {
                    lateCount++;
                }
            } else {
                absentCount++;
            }
        }

        // Crear respuesta con estadísticas
        return buildClassAttendanceResponse(classDto, createdAttendances, presentCount, lateCount, absentCount);
    }

    private ClassAttendanceResponseDTO buildClassAttendanceResponse(ClassAttendanceDTO classDto,
            List<AttendanceDTO> attendances, int presentCount, int lateCount, int absentCount) {

        ClassAttendanceResponseDTO response = new ClassAttendanceResponseDTO();
        response.setDate(classDto.getDate());
        response.setClassName("Aula " + classDto.getClassroomId()); // Simplificado por ahora
        response.setSubjectName("Materia " + classDto.getSubjectId()); // Simplificado por ahora
        response.setTeacherName("Profesor " + classDto.getTeacherId()); // Simplificado por ahora
        response.setTotalStudents(classDto.getStudentsAttendance().size());
        response.setPresentCount(presentCount);
        response.setLateCount(lateCount);
        response.setAbsentCount(absentCount);
        response.setAttendances(attendances);

        return response;
    }

    private AttendanceDTO convertToDto(Attendance obj) {
        AttendanceDTO dto = modelMapper.map(obj, AttendanceDTO.class);

        // Manually map the IDs from the related entities
        if (obj.getClassroom() != null) {
            dto.setClassroomId(obj.getClassroom().getIdClassroom());
        }
        if (obj.getSchedule() != null) {
            dto.setScheduleId(obj.getSchedule().getIdSchedule());
        }
        if (obj.getStudent() != null) {
            dto.setStudentId(obj.getStudent().getIdStudent());
        }

        return dto;
    }

    private Attendance convertToEntity(AttendanceDTO dto) {
        Attendance attendance = new Attendance();
        attendance.setIdAttendance(dto.getIdAttendance());

        // Convertir fecha string a LocalDate - siempre asegurar que tenga un valor
        if (dto.getDate() != null && !dto.getDate().trim().isEmpty()) {
            attendance.setDate(java.time.LocalDate.parse(dto.getDate()));
        } else {
            // Si no se proporciona fecha, usar la fecha actual
            attendance.setDate(java.time.LocalDate.now());
        }

        // Convertir hora string a LocalDate (nota: en el modelo está como LocalDate,
        // debería ser LocalTime) - siempre asegurar que tenga un valor
        if (dto.getEntryTime() != null && !dto.getEntryTime().trim().isEmpty()) {
            // Para mantener consistencia, usar la misma fecha que date
            attendance.setEntryTime(attendance.getDate());
        } else {
            // Si no hay entryTime, usar la misma fecha que date
            attendance.setEntryTime(attendance.getDate());
        }

        attendance.setPresent(dto.isPresent());
        attendance.setLate(dto.isLate());

        // Crear objetos Classroom y Schedule con solo el ID
        if (dto.getClassroomId() != null) {
            com.systems.model.Classroom classroom = new com.systems.model.Classroom();
            classroom.setIdClassroom(dto.getClassroomId());
            attendance.setClassroom(classroom);
        }

        if (dto.getScheduleId() != null) {
            com.systems.model.Schedule schedule = new com.systems.model.Schedule();
            schedule.setIdSchedule(dto.getScheduleId());
            attendance.setSchedule(schedule);
        }

        // Crear objeto Student con solo el ID
        if (dto.getStudentId() != null) {
            com.systems.model.Student student = new com.systems.model.Student();
            student.setIdStudent(dto.getStudentId());
            attendance.setStudent(student);
        }

        return attendance;
    }

}
