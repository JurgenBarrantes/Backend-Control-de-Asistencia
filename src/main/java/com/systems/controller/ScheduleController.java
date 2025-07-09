package com.systems.controller;

import java.net.URI;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import com.systems.dto.ClassroomDTO;
import com.systems.dto.EnrolledStudentDTO;
import com.systems.dto.PersonDTO;
import com.systems.dto.ScheduleDTO;
import com.systems.model.Classroom;
import com.systems.model.Enrollment;
import com.systems.model.Schedule;
import com.systems.service.IEnrollmentService;
import com.systems.service.IScheduleService;

import lombok.RequiredArgsConstructor;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
// @CrossOrigin(origins = "*")
public class ScheduleController { // es para manejar las solicitudes relacionadas con los horarios
	private final IScheduleService service;
	private final IEnrollmentService enrollmentService;

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
			String sortField = sortBy != null ? sortBy : "idSchedule";
			String sortDir = sortDirection != null ? sortDirection : "asc";
			
			// Crear Pageable con sorting
			Sort sort = sortDir.equalsIgnoreCase("desc") ? 
				Sort.by(sortField).descending() : 
				Sort.by(sortField).ascending();
			Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
			
			// Usar el método que hace fetch join de classroom
			Page<Schedule> entityPage = service.findAllWithClassroom(pageable);
			Page<ScheduleDTO> dtoPage = entityPage.map(this::convertToDto);
			return ResponseEntity.ok(dtoPage);
		} else {
			// Sin parámetros de paginación, devolver lista completa
			List<ScheduleDTO> list = service.findAll().stream().map(this::convertToDto).toList();
			return ResponseEntity.ok(list);
		}
	}

	@GetMapping("/{id}/students")
    public ResponseEntity<List<EnrolledStudentDTO>> getEnrolledStudents(@PathVariable("id") Integer id) throws Exception {
        // 1. Buscar el horario para obtener el aula
        Schedule schedule = service.findById(id);
        if (schedule.getClassroom() == null) {
            // Si el horario no tiene un aula asociada, no puede tener estudiantes inscritos
            return ResponseEntity.ok(List.of());
        }
        Integer classroomId = schedule.getClassroom().getIdClassroom();

        // 2. Buscar todas las inscripciones para ese aula
        List<Enrollment> enrollments = enrollmentService.findByClassroomId(classroomId);

        // 3. Mapear los resultados al DTO deseado
        List<EnrolledStudentDTO> enrolledStudents = enrollments.stream()
                .map(enrollment -> {
                    // Crear el PersonDTO a partir de la persona del estudiante
                    PersonDTO personDto = new PersonDTO();
                    personDto.setIdPerson(enrollment.getStudent().getPerson().getIdPerson());
                    personDto.setDni(enrollment.getStudent().getPerson().getDni());
                    personDto.setFirstName(enrollment.getStudent().getPerson().getFirstName());
                    personDto.setLastName(enrollment.getStudent().getPerson().getLastName());
                    personDto.setEmail(enrollment.getStudent().getPerson().getEmail());
                    personDto.setPhone(enrollment.getStudent().getPerson().getPhone());
                    if (enrollment.getStudent().getPerson().getBirthdate() != null) {
                        personDto.setBirthdate(enrollment.getStudent().getPerson().getBirthdate().toString());
                    }
                    personDto.setGender(enrollment.getStudent().getPerson().getGender());
                    personDto.setAddress(enrollment.getStudent().getPerson().getAddress());

                    // Crear el DTO principal
                    return new EnrolledStudentDTO(
                            enrollment.getIdEnrollment(),
                            enrollment.getStudent().getIdStudent(),
                            personDto);
                })
                .toList();

        return ResponseEntity.ok(enrolledStudents);
    }

	@GetMapping("/{id}")
	public ResponseEntity<ScheduleDTO> findById(@PathVariable("id") Integer id) throws Exception {
		// Publisher obj = service.findById(id);
		// PublisherDTO obj = modelMapper.map(service.findById(id), PublisherDTO.class);
		ScheduleDTO obj = convertToDto(service.findById(id));
		return ResponseEntity.ok(obj);
	}

	@PostMapping
	public ResponseEntity<Void> save(@RequestBody ScheduleDTO dto) throws Exception {
		// TEMPORAL: Generar ID manualmente hasta que se configure AUTO_INCREMENT en la
		// DB
		if (dto.getIdSchedule() == null) {
			Integer nextId = getNextScheduleId();
			dto.setIdSchedule(nextId);
		}

		Schedule obj = service.save(convertToEntity(dto));
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(obj.getIdSchedule()).toUri();
		return ResponseEntity.created(location).build();
	}

	// TEMPORAL: Método para generar ID manualmente hasta que se configure
	// AUTO_INCREMENT
	private Integer getNextScheduleId() {
		try {
			List<Schedule> allSchedules = service.findAll();
			if (allSchedules.isEmpty()) {
				return 1;
			}
			// Obtener el ID máximo y sumar 1
			Integer maxId = allSchedules.stream()
					.mapToInt(Schedule::getIdSchedule)
					.max()
					.orElse(0);
			return maxId + 1;
		} catch (Exception e) {
			// Si hay error, empezar desde 1
			return 1;
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<ScheduleDTO> update(@PathVariable("id") Integer id, @RequestBody ScheduleDTO dto)
			throws Exception {
		// Publisher obj = service.update(modelMapper.map(dto,Publisher.class), id);
		// PublisherDTO dto1 = modelMapper.map(obj, PublisherDTO.class);
		// return ResponseEntity.ok(dto1);
		dto.setIdSchedule(id);
		Schedule obj = service.update(convertToEntity(dto), id);
		ScheduleDTO dto1 = convertToDto(obj);
		return ResponseEntity.ok(dto1);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Integer id)
			throws Exception {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("hateoas/{id}")
	public EntityModel<ScheduleDTO> findByIdHateoas(@PathVariable("id") Integer id) throws Exception {
		Schedule obj = service.findById(id);
		EntityModel<ScheduleDTO> resource = EntityModel.of(convertToDto(obj));

		WebMvcLinkBuilder link1 = linkTo(methodOn(this.getClass()).findById(id));
		WebMvcLinkBuilder link2 = linkTo(methodOn(this.getClass()).findAll(null, null, null, null));
		resource.add(link1.withRel("schedule-self-info"));
		resource.add(link2.withRel("schedule-all-info"));

		return resource;
	}
	private ScheduleDTO convertToDto(Schedule obj) {
		ScheduleDTO dto = new ScheduleDTO();
		dto.setIdSchedule(obj.getIdSchedule());
		dto.setDayOfWeek(obj.getDayOfWeek() != null ? obj.getDayOfWeek().toString() : null);
		dto.setStartTime(obj.getStartTime() != null ? obj.getStartTime().toString() : null);
		dto.setEndTime(obj.getEndTime() != null ? obj.getEndTime().toString() : null);
		
		// Convertir classroom si existe - versión básica sin referencias circulares
		if (obj.getClassroom() != null) {
			ClassroomDTO classroomDTO = new ClassroomDTO();
			classroomDTO.setIdClassroom(obj.getClassroom().getIdClassroom());
			classroomDTO.setName(obj.getClassroom().getName());
			// Los demás campos quedan como null y no se serializarán por @JsonInclude(NON_NULL)
			dto.setClassroom(classroomDTO);
		}
		
		// Subject está comentado en el entity, no establecer nada (quedará null y no se serializará)
		
		return dto;
	}

	private Schedule convertToEntity(ScheduleDTO dto) {
		// Validar campos obligatorios
		if (dto.getDayOfWeek() == null || dto.getDayOfWeek().trim().isEmpty()) {
			throw new IllegalArgumentException("El día de la semana es obligatorio");
		}
		if (dto.getStartTime() == null || dto.getStartTime().trim().isEmpty()) {
			throw new IllegalArgumentException("La hora de inicio es obligatoria");
		}
		if (dto.getEndTime() == null || dto.getEndTime().trim().isEmpty()) {
			throw new IllegalArgumentException("La hora de fin es obligatoria");
		}

		Schedule schedule = new Schedule();

		// Siempre establecer el ID (será generado en POST o vendrá del path en PUT)
		schedule.setIdSchedule(dto.getIdSchedule());

		try {
			schedule.setDayOfWeek(java.time.DayOfWeek.valueOf(dto.getDayOfWeek().toUpperCase()));
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Día de la semana inválido: " + dto.getDayOfWeek());
		}

		try {
			schedule.setStartTime(java.time.LocalTime.parse(dto.getStartTime()));
		} catch (Exception e) {
			throw new IllegalArgumentException("Formato de hora de inicio inválido: " + dto.getStartTime());
		}

		try {
			schedule.setEndTime(java.time.LocalTime.parse(dto.getEndTime()));
		} catch (Exception e) {
			throw new IllegalArgumentException("Formato de hora de fin inválido: " + dto.getEndTime());
		}

		// Manejar classroom - TEMPORAL: usar ID 1 como default cuando no se proporciona
		if (dto.getClassroom() != null && dto.getClassroom().getIdClassroom() != null) {
			Classroom classroom = new Classroom();
			classroom.setIdClassroom(dto.getClassroom().getIdClassroom());
			schedule.setClassroom(classroom);
		} else {
			// TEMPORAL: Usar classroom con ID 1 como default (debe existir en la DB)
			Classroom defaultClassroom = new Classroom();
			defaultClassroom.setIdClassroom(1);
			schedule.setClassroom(defaultClassroom);
		}

		return schedule;
	}
}
