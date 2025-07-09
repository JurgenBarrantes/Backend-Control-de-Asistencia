package com.systems.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;

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
import com.systems.dto.SimpleScheduleDTO;
import com.systems.dto.SubjectDTO;
import com.systems.dto.TeacherDTO;
import com.systems.model.Classroom;
import com.systems.model.Schedule;
import com.systems.model.Subject;
import com.systems.model.Teacher;
import com.systems.service.IClassroomService;
import com.systems.service.IScheduleService;
import com.systems.service.ISubjectService;
import com.systems.service.ITeacherService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/classrooms")
@RequiredArgsConstructor
// @CrossOrigin(origins = "*")
public class ClassroomController { // este controlador es para manejar las solicitudes relacionadas con las aulas
    private final IClassroomService service;
    private final ITeacherService teacherService;
    private final ISubjectService subjectService;
    private final IScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<?> findAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection) throws Exception {

        // Si se proporcionan parámetros de paginación, usar paginación
        if (page != null || size != null) {
            // Convertir página de base-1 a base-0 para Spring Data
            // Si page es null, usar 1 como default (primera página en base-1)
            int userPageNumber = page != null ? page : 1;
            int springPageNumber = Math.max(0, userPageNumber - 1);
            int pageSize = size != null ? size : 10;
            String sortField = sortBy != null ? sortBy : "idClassroom";
            String sortDir = sortDirection != null ? sortDirection : "asc";

            // Crear Pageable con sorting
            Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortField).descending()
                    : Sort.by(sortField).ascending();
            Pageable pageable = PageRequest.of(springPageNumber, pageSize, sort);

            // Usar el método que hace fetch join de teacher y subject
            Page<Classroom> entityPage = service.findAllWithTeacherAndSubject(pageable);
            Page<ClassroomDTO> dtoPage = entityPage.map(this::convertToDto);

            // Crear una respuesta personalizada que ajuste el número de página para mostrar
            // base-1
            return ResponseEntity.ok(new CustomPageResponse<>(dtoPage, userPageNumber));
        } else {
            // Sin parámetros de paginación, devolver lista completa
            List<ClassroomDTO> list = service.findAll().stream().map(this::convertToDto).toList();
            return ResponseEntity.ok(list);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassroomDTO> findById(@PathVariable("id") Integer id) throws Exception {
        ClassroomDTO obj = convertToDto(service.findById(id));
        return ResponseEntity.ok(obj);
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody ClassroomDTO dto) throws Exception {
        Classroom obj = service.save(convertToEntity(dto));
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(obj.getIdClassroom()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClassroomDTO> update(@PathVariable("id") Integer id, @RequestBody ClassroomDTO dto)
            throws Exception {
        dto.setIdClassroom(id);
        Classroom obj = service.update(convertToEntity(dto), id);
        ClassroomDTO dto1 = convertToDto(obj);
        return ResponseEntity.ok(dto1);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) throws Exception {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("hateoas/{id}")
    public EntityModel<ClassroomDTO> findByIdHateoas(@PathVariable("id") Integer id) throws Exception {
        Classroom obj = service.findById(id);
        EntityModel<ClassroomDTO> resource = EntityModel.of(convertToDto(obj));

        WebMvcLinkBuilder link1 = linkTo(methodOn(this.getClass()).findById(id));
        WebMvcLinkBuilder link2 = linkTo(methodOn(this.getClass()).findAll(null, null, null, null));
        resource.add(link1.withRel("classroom-self-info"));
        resource.add(link2.withRel("classroom-all-info"));

        return resource;
    }

    // TEMPORAL: Endpoint para debug del classroom 12
    @GetMapping("/{id}/debug-schedules")
    public ResponseEntity<?> debugSchedulesForClassroom(@PathVariable("id") Integer id) throws Exception {
        try {
            Classroom classroom = service.findById(id);
            List<Schedule> schedules = scheduleService.findByClassroom(classroom);

            return ResponseEntity.ok(Map.of(
                    "classroomId", id,
                    "classroomName", classroom.getName(),
                    "schedulesCount", schedules.size(),
                    "schedules", schedules.stream().map(s -> Map.of(
                            "idSchedule", s.getIdSchedule(),
                            "dayOfWeek", s.getDayOfWeek().toString(),
                            "startTime", s.getStartTime().toString(),
                            "endTime", s.getEndTime().toString(),
                            "classroomId", s.getClassroom() != null ? s.getClassroom().getIdClassroom() : null))
                            .toList()));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("error", e.getMessage()));
        }
    }

    private ClassroomDTO convertToDto(Classroom obj) {
        ClassroomDTO dto = new ClassroomDTO();
        dto.setIdClassroom(obj.getIdClassroom());
        dto.setName(obj.getName());

        // Convertir Teacher a TeacherDTO
        if (obj.getTeacher() != null) {
            TeacherDTO teacherDTO = new TeacherDTO();
            teacherDTO.setIdTeacher(obj.getTeacher().getIdTeacher());

            // Obtener datos de la persona asociada al teacher
            if (obj.getTeacher().getPerson() != null) {
                teacherDTO.setFirstName(obj.getTeacher().getPerson().getFirstName());
                teacherDTO.setLastName(obj.getTeacher().getPerson().getLastName());
                teacherDTO.setFullName(
                        obj.getTeacher().getPerson().getFirstName() + " " + obj.getTeacher().getPerson().getLastName());
                teacherDTO.setDni(obj.getTeacher().getPerson().getDni());
                teacherDTO.setEmail(obj.getTeacher().getPerson().getEmail());
                teacherDTO.setPhone(obj.getTeacher().getPerson().getPhone());
                teacherDTO.setBirthdate(obj.getTeacher().getPerson().getBirthdate() != null
                        ? obj.getTeacher().getPerson().getBirthdate().toString()
                        : null);
                teacherDTO.setGender(obj.getTeacher().getPerson().getGender());
                teacherDTO.setAddress(obj.getTeacher().getPerson().getAddress());
            }

            dto.setTeacher(teacherDTO);
        }

        // Convertir Subject a SubjectDTO
        if (obj.getSubject() != null) {
            SubjectDTO subjectDTO = new SubjectDTO();
            subjectDTO.setIdSubject(obj.getSubject().getIdSubject());
            subjectDTO.setName(obj.getSubject().getName());

            dto.setSubject(subjectDTO);
        }

        // Convertir Schedules a SimpleScheduleDTOs
        try {
            List<Schedule> schedules = scheduleService.findByClassroom(obj);
            List<SimpleScheduleDTO> scheduleDTOs = schedules.stream().map(schedule -> {
                SimpleScheduleDTO scheduleDTO = new SimpleScheduleDTO();
                scheduleDTO.setIdSchedule(schedule.getIdSchedule());
                scheduleDTO.setDayOfWeek(schedule.getDayOfWeek() != null ? schedule.getDayOfWeek().toString() : null);
                scheduleDTO.setStartTime(schedule.getStartTime() != null ? schedule.getStartTime().toString() : null);
                scheduleDTO.setEndTime(schedule.getEndTime() != null ? schedule.getEndTime().toString() : null);
                return scheduleDTO;
            }).toList();
            dto.setSchedules(scheduleDTOs);
        } catch (Exception e) {
            // En caso de error, establecer una lista vacía
            dto.setSchedules(List.of());
        }

        return dto;
    }

    private Classroom convertToEntity(ClassroomDTO dto) {
        // Validar campos obligatorios
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del aula es obligatorio");
        }

        Classroom classroom = new Classroom();
        classroom.setIdClassroom(dto.getIdClassroom()); // null para POST, ID para PUT
        classroom.setName(dto.getName().trim());

        // Manejar Teacher - obtener ID del DTO embebido
        if (dto.getTeacher() != null && dto.getTeacher().getIdTeacher() != null) {
            try {
                Teacher teacher = teacherService.findById(dto.getTeacher().getIdTeacher());
                classroom.setTeacher(teacher);
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        "No se encontró el profesor con ID: " + dto.getTeacher().getIdTeacher());
            }
        } else {
            throw new IllegalArgumentException("Se debe proporcionar un profesor válido con teacher.idTeacher");
        }

        // Manejar Subject - obtener ID del DTO embebido
        if (dto.getSubject() != null && dto.getSubject().getIdSubject() != null) {
            try {
                Subject subject = subjectService.findById(dto.getSubject().getIdSubject());
                classroom.setSubject(subject);
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        "No se encontró la materia con ID: " + dto.getSubject().getIdSubject());
            }
        } else {
            throw new IllegalArgumentException("Se debe proporcionar una materia válida con subject.idSubject");
        }

        return classroom;
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
// Clase helper para respuesta de paginación personalizada
class CustomPageResponse<T> {
    private java.util.List<T> content;
    private CustomPageable pageable;
    private boolean last;
    private int totalElements;
    private int totalPages;
    private boolean first;
    private int size;
    private int number;
    private org.springframework.data.domain.Sort sort;
    private int numberOfElements;
    private boolean empty;

    public CustomPageResponse(org.springframework.data.domain.Page<T> page, int displayPageNumber) {
        this.content = page.getContent();
        this.totalElements = (int) page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.size = page.getSize();
        this.number = displayPageNumber; // Número de página que se muestra (base-1)
        this.sort = page.getSort();
        this.numberOfElements = page.getNumberOfElements();
        this.empty = page.isEmpty();

        // Ajustar first/last basado en el número de página mostrado (base-1)
        this.first = (displayPageNumber == 1);
        this.last = (displayPageNumber == this.totalPages);

        // Crear pageable personalizado con offset correcto
        long correctOffset = (long) (displayPageNumber - 1) * page.getSize();
        this.pageable = new CustomPageable(displayPageNumber, page.getSize(), page.getSort(), correctOffset);
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
// Clase helper para Pageable personalizado
class CustomPageable {
    private int pageNumber;
    private int pageSize;
    private org.springframework.data.domain.Sort sort;
    private long offset;
    private boolean paged = true;
    private boolean unpaged = false;

    public CustomPageable(int pageNumber, int pageSize, org.springframework.data.domain.Sort sort, long offset) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.sort = sort;
        this.offset = offset;
    }
}
