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
import com.systems.dto.CustomPageResponse;
import com.systems.dto.EnrollmentDTO;
import com.systems.dto.StudentDTO;
import com.systems.dto.SubjectDTO;
import com.systems.dto.TeacherDTO;
import com.systems.model.Enrollment;
import com.systems.service.IEnrollmentService;
import com.systems.service.IStudentService;
import com.systems.service.IClassroomService;

import lombok.RequiredArgsConstructor;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {
    private final IEnrollmentService service;
    private final IStudentService studentService;
    private final IClassroomService classroomService;

    @GetMapping
    public ResponseEntity<?> findAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection) throws Exception {

        // Si se proporcionan parámetros de paginación, usar paginación
        if (page != null || size != null) {
            // Convertir página de base-1 a base-0 para Spring Data
            int userPageNumber = page != null ? page : 1;
            int springPageNumber = Math.max(0, userPageNumber - 1);
            int pageSize = size != null ? size : 10;
            String sortField = sortBy != null ? sortBy : "idEnrollment";
            String sortDir = sortDirection != null ? sortDirection : "asc";

            // Crear Pageable con sorting
            Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortField).descending()
                    : Sort.by(sortField).ascending();
            Pageable pageable = PageRequest.of(springPageNumber, pageSize, sort);

            // Usar el método que hace fetch join de student y classroom
            Page<Enrollment> entityPage = service.findAllWithStudentAndClassroom(pageable);
            Page<EnrollmentDTO> dtoPage = entityPage.map(this::convertToDto);

            // Crear una respuesta personalizada que ajuste el número de página para mostrar
            // base-1
            return ResponseEntity.ok(new CustomPageResponse<>(dtoPage, userPageNumber));
        } else {
            // Sin parámetros de paginación, devolver lista completa
            List<EnrollmentDTO> list = service.findAll().stream().map(this::convertToDto).toList();
            return ResponseEntity.ok(list);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentDTO> findById(@PathVariable("id") Integer id) throws Exception {
        EnrollmentDTO obj = convertToDto(service.findById(id));
        return ResponseEntity.ok(obj);
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody EnrollmentDTO dto) throws Exception {
        Enrollment obj = service.save(convertToEntity(dto));

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(obj.getIdEnrollment()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnrollmentDTO> update(@PathVariable("id") Integer id, @RequestBody EnrollmentDTO dto)
            throws Exception {
        dto.setIdEnrollment(id);
        Enrollment obj = service.update(convertToEntity(dto), id);
        EnrollmentDTO dto1 = convertToDto(obj);
        return ResponseEntity.ok(dto1);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) throws Exception {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("hateoas/{id}")
    public EntityModel<EnrollmentDTO> findByIdHateoas(@PathVariable("id") Integer id) throws Exception {
        Enrollment obj = service.findById(id);
        EntityModel<EnrollmentDTO> resource = EntityModel.of(convertToDto(obj));

        WebMvcLinkBuilder link1 = linkTo(methodOn(this.getClass()).findById(id));
        WebMvcLinkBuilder link2 = linkTo(methodOn(this.getClass()).findAll(null, null, null, null));
        resource.add(link1.withRel("enrollment-self-info"));
        resource.add(link2.withRel("enrollment-all-info"));

        return resource;
    }

    private EnrollmentDTO convertToDto(Enrollment obj) {
        if (obj == null)
            return null;

        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setIdEnrollment(obj.getIdEnrollment());

        // Convertir Student
        if (obj.getStudent() != null) {
            StudentDTO studentDto = new StudentDTO();
            studentDto.setIdStudent(obj.getStudent().getIdStudent());

            // Convertir datos de Person del Student
            if (obj.getStudent().getPerson() != null) {
                studentDto.setFirstName(obj.getStudent().getPerson().getFirstName());
                studentDto.setLastName(obj.getStudent().getPerson().getLastName());
                studentDto.setFullName(
                        obj.getStudent().getPerson().getFirstName() + " " + obj.getStudent().getPerson().getLastName());
                studentDto.setDni(obj.getStudent().getPerson().getDni());
                studentDto.setEmail(obj.getStudent().getPerson().getEmail());
                studentDto.setPhone(obj.getStudent().getPerson().getPhone());
                studentDto.setAddress(obj.getStudent().getPerson().getAddress());
                studentDto.setBirthdate(obj.getStudent().getPerson().getBirthdate().toString());
                studentDto.setGender(obj.getStudent().getPerson().getGender());
            }

            dto.setStudent(studentDto);
        }

        // Convertir Classroom
        if (obj.getClassroom() != null) {
            ClassroomDTO classroomDto = new ClassroomDTO();
            classroomDto.setIdClassroom(obj.getClassroom().getIdClassroom());
            classroomDto.setName(obj.getClassroom().getName());

            // Convertir Teacher del Classroom
            if (obj.getClassroom().getTeacher() != null) {
                TeacherDTO teacherDto = new TeacherDTO();
                teacherDto.setIdTeacher(obj.getClassroom().getTeacher().getIdTeacher());

                // Convertir datos de Person del Teacher
                if (obj.getClassroom().getTeacher().getPerson() != null) {
                    teacherDto.setFirstName(obj.getClassroom().getTeacher().getPerson().getFirstName());
                    teacherDto.setLastName(obj.getClassroom().getTeacher().getPerson().getLastName());
                    teacherDto.setFullName(obj.getClassroom().getTeacher().getPerson().getFirstName() + " "
                            + obj.getClassroom().getTeacher().getPerson().getLastName());
                    teacherDto.setDni(obj.getClassroom().getTeacher().getPerson().getDni());
                    teacherDto.setEmail(obj.getClassroom().getTeacher().getPerson().getEmail());
                    teacherDto.setPhone(obj.getClassroom().getTeacher().getPerson().getPhone());
                    teacherDto.setAddress(obj.getClassroom().getTeacher().getPerson().getAddress());
                    teacherDto.setBirthdate(obj.getClassroom().getTeacher().getPerson().getBirthdate().toString());
                    teacherDto.setGender(obj.getClassroom().getTeacher().getPerson().getGender());
                }

                classroomDto.setTeacher(teacherDto);
            }

            // Convertir Subject del Classroom
            if (obj.getClassroom().getSubject() != null) {
                SubjectDTO subjectDto = new SubjectDTO();
                subjectDto.setIdSubject(obj.getClassroom().getSubject().getIdSubject());
                subjectDto.setName(obj.getClassroom().getSubject().getName());

                classroomDto.setSubject(subjectDto);
            }

            dto.setClassroom(classroomDto);
        }

        return dto;
    }

    private Enrollment convertToEntity(EnrollmentDTO dto) {
        if (dto == null)
            return null;

        Enrollment enrollment = new Enrollment();
        enrollment.setIdEnrollment(dto.getIdEnrollment());

        // Para crear/actualizar, necesitamos buscar las entidades por ID
        if (dto.getStudent() != null && dto.getStudent().getIdStudent() != null) {
            try {
                enrollment.setStudent(studentService.findById(dto.getStudent().getIdStudent()));
            } catch (Exception e) {
                // Manejar el error apropiadamente
                throw new RuntimeException("Student not found with id: " + dto.getStudent().getIdStudent());
            }
        }

        if (dto.getClassroom() != null && dto.getClassroom().getIdClassroom() != null) {
            try {
                enrollment.setClassroom(classroomService.findById(dto.getClassroom().getIdClassroom()));
            } catch (Exception e) {
                // Manejar el error apropiadamente
                throw new RuntimeException("Classroom not found with id: " + dto.getClassroom().getIdClassroom());
            }
        }

        return enrollment;
    }
}
