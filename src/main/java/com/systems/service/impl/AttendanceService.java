package com.systems.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.systems.dto.AttendanceDTO;
import com.systems.dto.AttendanceReportDTO;
import com.systems.dto.BulkAttendanceDTO;
import com.systems.dto.ClassAttendanceDTO;
import com.systems.dto.ClassAttendanceResponseDTO;
import com.systems.dto.StudentAttendanceDTO;
import com.systems.model.Attendance;
import com.systems.model.Classroom;
import com.systems.model.Schedule;
import com.systems.model.Student;
import com.systems.model.TardinessRule;
import com.systems.repo.IAttendanceRepo;
import com.systems.repo.IGenericRepo;
import com.systems.repo.IScheduleRepo;
import com.systems.repo.ITardinessRuleRepo;
import com.systems.service.IAttendanceService;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceService extends GenericService<Attendance, Integer> implements IAttendanceService {
    private final IAttendanceRepo repo;
    private final IScheduleRepo scheduleRepo;
    private final ITardinessRuleRepo tardinessRuleRepo;
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
        Attendance attendance = modelMapper.map(dto, Attendance.class);
        return attendance;
    }

    @Override
    public List<ClassAttendanceResponseDTO> getAttendanceByClass(Integer classroomId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAttendanceByClass'");
    }

    public List<AttendanceReportDTO> getAttendanceReport(Integer classroomId, String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return repo.getAttendanceReport(classroomId, start, end);
    }

    @Transactional
    @Override
    public void saveBulk(BulkAttendanceDTO bulkAttendanceDTO) throws Exception {
        LocalDate currentDate = LocalDate.now();
        LocalTime entryTime = LocalTime.now();

        Schedule schedule = scheduleRepo.findById(bulkAttendanceDTO.getScheduleId())
                .orElseThrow(() -> new Exception("Schedule not found"));
        LocalTime scheduleStartTime = schedule.getStartTime();

        Optional<TardinessRule> ruleOpt = tardinessRuleRepo
                .findByClassroom_IdClassroom(bulkAttendanceDTO.getClassroomId());
        int tardinessThreshold = ruleOpt.map(TardinessRule::getTardinnessThresholdMinutes).orElse(10); // Default 10
                                                                                                       // mins
        int absenceThreshold = ruleOpt.map(TardinessRule::getAbsenceThresholdMinutes).orElse(30); // Default 30 mins

        List<Attendance> attendancesToSave = new ArrayList<>();

        for (StudentAttendanceDTO studentAttendance : bulkAttendanceDTO.getStudents()) {
            Attendance attendance = new Attendance();
            attendance.setDate(currentDate);
            attendance.setEntryTime(entryTime);

            Classroom classroom = new Classroom();
            classroom.setIdClassroom(bulkAttendanceDTO.getClassroomId());
            attendance.setClassroom(classroom);

            attendance.setSchedule(schedule);

            Student student = new Student();
            student.setIdStudent(studentAttendance.getStudentId());
            attendance.setStudent(student);

            long minutesLate = ChronoUnit.MINUTES.between(scheduleStartTime, entryTime);

            if (!studentAttendance.getIsPresent()) {
                attendance.setPresent(false);
                attendance.setLate(false);
            } else if (minutesLate > absenceThreshold) {
                attendance.setPresent(false); // Considered absent
                attendance.setLate(false);
            } else if (minutesLate > tardinessThreshold) {
                attendance.setPresent(true);
                attendance.setLate(true); // Tardy
            } else {
                attendance.setPresent(true);
                attendance.setLate(false); // On time
            }
            attendancesToSave.add(attendance);
        }
        repo.saveAll(attendancesToSave);
    }
}
