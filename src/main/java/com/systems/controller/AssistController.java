package com.systems.controller;

import java.net.URI;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
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

import com.systems.dto.AssistDTO;
import com.systems.model.Assist;
import com.systems.model.Attendance;
import com.systems.model.Enrollment;
import com.systems.service.IAssistService;

import lombok.RequiredArgsConstructor;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/assists")
@RequiredArgsConstructor
public class AssistController {
    private final IAssistService service;

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
            String sortField = sortBy != null ? sortBy : "idAssist";
            String sortDir = sortDirection != null ? sortDirection : "asc";
            
            Page<Assist> entityPage = service.findAllPaginated(pageNumber, pageSize, sortField, sortDir);
            Page<AssistDTO> dtoPage = entityPage.map(this::convertToDto);
            return ResponseEntity.ok(dtoPage);
        } else {
            // Sin parámetros de paginación, devolver lista completa
            List<AssistDTO> list = service.findAll().stream().map(this::convertToDto).toList();
            return ResponseEntity.ok(list);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssistDTO> findById(@PathVariable("id") Integer id) throws Exception {
        AssistDTO obj = convertToDto(service.findById(id));
        return ResponseEntity.ok(obj);
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody AssistDTO dto) throws Exception {
        Assist obj = service.save(convertToEntity(dto));

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(obj.getIdAssist()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssistDTO> update(@PathVariable("id") Integer id, @RequestBody AssistDTO dto)
            throws Exception {
        dto.setIdAssist(id);
        Assist obj = service.update(convertToEntity(dto), id);
        AssistDTO dto1 = convertToDto(obj);
        return ResponseEntity.ok(dto1);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) throws Exception {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("hateoas/{id}")
    public EntityModel<AssistDTO> findByIdHateoas(@PathVariable("id") Integer id) throws Exception {
        Assist obj = service.findById(id);
        EntityModel<AssistDTO> resource = EntityModel.of(convertToDto(obj));

        WebMvcLinkBuilder link1 = linkTo(methodOn(this.getClass()).findById(id));
        WebMvcLinkBuilder link2 = linkTo(methodOn(this.getClass()).findAll(null, null, null, null));
        resource.add(link1.withRel("assist-self-info"));
        resource.add(link2.withRel("assist-all-info"));

        return resource;
    }

    private AssistDTO convertToDto(Assist obj) {
        AssistDTO dto = new AssistDTO();
        dto.setIdAssist(obj.getIdAssist());
        dto.setStatus(obj.getStatus());
        
        // Mapear las relaciones
        if (obj.getAttendance() != null) {
            dto.setAttendanceId(obj.getAttendance().getIdAttendance());
            // Información adicional de la asistencia
            if (obj.getAttendance().getDate() != null) {
                dto.setAttendanceDate(obj.getAttendance().getDate().toString());
            }
            if (obj.getAttendance().getEntryTime() != null) {
                dto.setAttendanceEntryTime(obj.getAttendance().getEntryTime().toString());
            }
            dto.setAttendancePresent(obj.getAttendance().isPresent());
            dto.setAttendanceLate(obj.getAttendance().isLate());
        }
        
        if (obj.getEnrollment() != null) {
            dto.setEnrollmentId(obj.getEnrollment().getIdEnrollment());
            
            // Información adicional del enrollment - estudiante
            if (obj.getEnrollment().getStudent() != null) {
                dto.setStudentId(obj.getEnrollment().getStudent().getIdStudent());
                if (obj.getEnrollment().getStudent().getPerson() != null) {
                    dto.setStudentFirstName(obj.getEnrollment().getStudent().getPerson().getFirstName());
                    dto.setStudentLastName(obj.getEnrollment().getStudent().getPerson().getLastName());
                    dto.setStudentFullName(obj.getEnrollment().getStudent().getPerson().getFirstName() + " " + obj.getEnrollment().getStudent().getPerson().getLastName());
                }
            }
            
            // Información adicional del enrollment - aula/classroom
            if (obj.getEnrollment().getClassroom() != null) {
                dto.setClassroomId(obj.getEnrollment().getClassroom().getIdClassroom());
                dto.setClassroomName(obj.getEnrollment().getClassroom().getName());
            }
        }
        
        return dto;
    }

    private Assist convertToEntity(AssistDTO dto) {
        Assist entity = new Assist();
        entity.setIdAssist(dto.getIdAssist());
        entity.setStatus(dto.getStatus());
        
        // Crear objetos de relaciones con solo el ID (referencias lazy)
        if (dto.getAttendanceId() != null) {
            Attendance attendance = new Attendance();
            attendance.setIdAttendance(dto.getAttendanceId());
            entity.setAttendance(attendance);
        }
        
        if (dto.getEnrollmentId() != null) {
            Enrollment enrollment = new Enrollment();
            enrollment.setIdEnrollment(dto.getEnrollmentId());
            entity.setEnrollment(enrollment);
        }
        
        return entity;
    }
}
