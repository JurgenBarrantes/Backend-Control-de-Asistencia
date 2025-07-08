package com.systems.controller;

import java.net.URI;
import java.util.List;

import org.modelmapper.ModelMapper;
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

import com.systems.dto.EnrollmentDTO;
import com.systems.model.Enrollment;
import com.systems.service.IEnrollmentService;

import lombok.RequiredArgsConstructor;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {
    private final IEnrollmentService service;
    private final ModelMapper modelMapper;

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
            String sortField = sortBy != null ? sortBy : "idEnrollment";
            String sortDir = sortDirection != null ? sortDirection : "asc";
            
            Page<Enrollment> entityPage = service.findAllPaginated(pageNumber, pageSize, sortField, sortDir);
            Page<EnrollmentDTO> dtoPage = entityPage.map(this::convertToDto);
            return ResponseEntity.ok(dtoPage);
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
        return modelMapper.map(obj, EnrollmentDTO.class);
    }

    private Enrollment convertToEntity(EnrollmentDTO dto) {
        return modelMapper.map(dto, Enrollment.class);
    }
}
