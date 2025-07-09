package com.systems.controller;

import java.net.URI;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.systems.dto.AttendanceDTO;
import com.systems.dto.BulkAttendanceDTO;
import com.systems.dto.ClassAttendanceDTO;
import com.systems.dto.ClassAttendanceResponseDTO;
import com.systems.dto.ClassroomDTO;
import com.systems.dto.SimpleScheduleDTO;
import com.systems.dto.StudentDTO;
import com.systems.dto.StudentAttendanceDTO;
import com.systems.dto.SubjectDTO;
import com.systems.dto.TeacherDTO;
import com.systems.model.Attendance;
import com.systems.model.Classroom;
import com.systems.model.Schedule;
import com.systems.model.Student;
import com.systems.model.TardinessRule;
import com.systems.service.IAttendanceService;
import com.systems.service.IScheduleService;
import com.systems.service.ITardinessRuleService;

import lombok.RequiredArgsConstructor;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/attendances")
@RequiredArgsConstructor
// @CrossOrigin(origins = "*")
public class AttendanceController { // es para manejar las solicitudes relacionadas con la asistencia
	private final IAttendanceService service;
	private final IScheduleService scheduleService;
	private final ITardinessRuleService tardinessRuleService;

	// @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
	@GetMapping
	public ResponseEntity<?> findAll(
			@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer size,
			@RequestParam(required = false) String sortBy,
			@RequestParam(required = false) String sortDirection) throws Exception {

		// Si se proporcionan parámetros de paginación, usar paginación
		if (page != null || size != null) {
			int pageNumber = page != null ? page : 0;
			int pageSize = size != null ? size : 10;
			String sortField = sortBy != null ? sortBy : "idAttendance";
			String sortDir = sortDirection != null ? sortDirection : "asc";

			Page<Attendance> entityPage = service.findAllPaginated(pageNumber, pageSize, sortField, sortDir);
			Page<AttendanceDTO> dtoPage = entityPage.map(this::convertToDto);
			return ResponseEntity.ok(dtoPage);
		} else {
			// Sin parámetros de paginación, devolver lista completa
			List<AttendanceDTO> list = service.findAll().stream().map(this::convertToDto).toList();
			return ResponseEntity.ok(list);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<AttendanceDTO> findById(@PathVariable("id") Integer id) throws Exception {
		AttendanceDTO obj = convertToDto(service.findById(id));
		return ResponseEntity.ok(obj);
	}

	@PostMapping
	public ResponseEntity<Void> save(@RequestBody AttendanceDTO dto) throws Exception {
		Attendance obj = service.save(convertToEntity(dto));

		// location: http://localhost:9091/attendances/{id}
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(obj.getIdAttendance()).toUri();
		return ResponseEntity.created(location).build();
	}

	@PostMapping("/bulk")
	public ResponseEntity<Void> saveBulkAttendance(@RequestBody BulkAttendanceDTO bulkAttendanceDTO) throws Exception {
		service.saveBulk(bulkAttendanceDTO);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<AttendanceDTO> update(@PathVariable("id") Integer id, @RequestBody AttendanceDTO dto)
			throws Exception {
		dto.setIdAttendance(id);
		Attendance obj = service.update(convertToEntity(dto), id);
		AttendanceDTO dto1 = convertToDto(obj);
		return ResponseEntity.ok(dto1);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Integer id)
			throws Exception {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("hateoas/{id}")
	public EntityModel<AttendanceDTO> findByIdHateoas(@PathVariable("id") Integer id) throws Exception {
		Attendance obj = service.findById(id);
		EntityModel<AttendanceDTO> resource = EntityModel.of(convertToDto(obj));

		WebMvcLinkBuilder link1 = linkTo(methodOn(this.getClass()).findById(id));
		WebMvcLinkBuilder link2 = linkTo(methodOn(this.getClass()).findAll(null, null, null, null));
		resource.add(link1.withRel("attendance-self-info"));
		resource.add(link2.withRel("attendance-all-info"));

		return resource;
	}

	@PostMapping("/bulk-class")
	public ResponseEntity<ClassAttendanceResponseDTO> saveClassAttendance(
			@RequestBody ClassAttendanceDTO classAttendanceDto)
			throws Exception {
		ClassAttendanceResponseDTO response = service.saveClassAttendance(classAttendanceDto);
		return ResponseEntity.ok(response);
	}

	private AttendanceDTO convertToDto(Attendance obj) {
		AttendanceDTO dto = new AttendanceDTO();
		dto.setIdAttendance(obj.getIdAttendance());

		if (obj.getDate() != null) {
			dto.setDate(obj.getDate().toString());
		}
		if (obj.getEntryTime() != null) {
			dto.setEntryTime(obj.getEntryTime().toString());
		}

		dto.setPresent(obj.isPresent());
		dto.setLate(obj.isLate());

		if (obj.getClassroom() != null) {
			dto.setClassroomId(obj.getClassroom().getIdClassroom());
			dto.setClassroom(convertClassroomToDto(obj.getClassroom()));
		}

		if (obj.getSchedule() != null) {
			dto.setScheduleId(obj.getSchedule().getIdSchedule());
			dto.setSchedule(convertScheduleToSimpleDto(obj.getSchedule()));
		}

		if (obj.getStudent() != null) {
			dto.setStudentId(obj.getStudent().getIdStudent());
			dto.setStudent(convertStudentToDto(obj.getStudent()));
		}

		return dto;
	}

	private Attendance convertToEntity(AttendanceDTO dto) {
		Attendance attendance = new Attendance();
		attendance.setIdAttendance(dto.getIdAttendance());

		if (dto.getDate() != null && !dto.getDate().trim().isEmpty()) {
			attendance.setDate(java.time.LocalDate.parse(dto.getDate()));
		} else {
			attendance.setDate(java.time.LocalDate.now());
		}

		if (dto.getEntryTime() != null && !dto.getEntryTime().trim().isEmpty()) {
			attendance.setEntryTime(null);
		} else {
			attendance.setEntryTime(null);
		}

		attendance.setPresent(dto.isPresent());
		attendance.setLate(dto.isLate());

		if (dto.getClassroomId() != null) {
			Classroom classroom = new Classroom();
			classroom.setIdClassroom(dto.getClassroomId());
			attendance.setClassroom(classroom);
		}

		if (dto.getScheduleId() != null) {
			Schedule schedule = new Schedule();
			schedule.setIdSchedule(dto.getScheduleId());
			attendance.setSchedule(schedule);
		}

		if (dto.getStudentId() != null) {
			Student student = new Student();
			student.setIdStudent(dto.getStudentId());
			attendance.setStudent(student);
		}

		return attendance;
	}

	// Métodos helper para convertir entidades a DTOs completos
	private ClassroomDTO convertClassroomToDto(Classroom classroom) {
		ClassroomDTO dto = new ClassroomDTO();
		dto.setIdClassroom(classroom.getIdClassroom());
		dto.setName(classroom.getName());

		// Convertir teacher si existe
		if (classroom.getTeacher() != null && classroom.getTeacher().getPerson() != null) {
			TeacherDTO teacherDTO = new TeacherDTO();
			teacherDTO.setIdTeacher(classroom.getTeacher().getIdTeacher());
			teacherDTO.setFirstName(classroom.getTeacher().getPerson().getFirstName());
			teacherDTO.setLastName(classroom.getTeacher().getPerson().getLastName());
			teacherDTO.setFullName(classroom.getTeacher().getPerson().getFirstName() + " "
					+ classroom.getTeacher().getPerson().getLastName());
			teacherDTO.setDni(classroom.getTeacher().getPerson().getDni());
			teacherDTO.setEmail(classroom.getTeacher().getPerson().getEmail());
			teacherDTO.setPhone(classroom.getTeacher().getPerson().getPhone());
			teacherDTO.setBirthdate(classroom.getTeacher().getPerson().getBirthdate() != null
					? classroom.getTeacher().getPerson().getBirthdate().toString()
					: null);
			teacherDTO.setGender(classroom.getTeacher().getPerson().getGender());
			teacherDTO.setAddress(classroom.getTeacher().getPerson().getAddress());
			dto.setTeacher(teacherDTO);
		}

		// Convertir subject si existe
		if (classroom.getSubject() != null) {
			SubjectDTO subjectDTO = new SubjectDTO();
			subjectDTO.setIdSubject(classroom.getSubject().getIdSubject());
			subjectDTO.setName(classroom.getSubject().getName());
			dto.setSubject(subjectDTO);
		}

		return dto;
	}

	private SimpleScheduleDTO convertScheduleToSimpleDto(Schedule schedule) {
		SimpleScheduleDTO dto = new SimpleScheduleDTO();
		dto.setIdSchedule(schedule.getIdSchedule());
		dto.setDayOfWeek(schedule.getDayOfWeek().toString());
		dto.setStartTime(schedule.getStartTime().toString());
		dto.setEndTime(schedule.getEndTime().toString());
		return dto;
	}

	private StudentDTO convertStudentToDto(Student student) {
		StudentDTO dto = new StudentDTO();
		dto.setIdStudent(student.getIdStudent());

		if (student.getPerson() != null) {
			dto.setFirstName(student.getPerson().getFirstName());
			dto.setLastName(student.getPerson().getLastName());
			dto.setFullName(student.getPerson().getFirstName() + " " + student.getPerson().getLastName());
			dto.setDni(student.getPerson().getDni());
			dto.setEmail(student.getPerson().getEmail());
			dto.setPhone(student.getPerson().getPhone());
			dto.setBirthdate(
					student.getPerson().getBirthdate() != null ? student.getPerson().getBirthdate().toString() : null);
			dto.setGender(student.getPerson().getGender());
			dto.setAddress(student.getPerson().getAddress());
		}

		return dto;
	}
}
