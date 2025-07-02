package com.systems.controller;

import java.net.URI;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.systems.dto.ClassroomDTO;
import com.systems.model.Classroom;
import com.systems.service.IClassroomService;

import lombok.RequiredArgsConstructor;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/classrooms")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClassroomController {
    private final IClassroomService service;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<List<ClassroomDTO>> findAll() throws Exception {
        List<ClassroomDTO> list = service.findAll().stream().map(this::convertToDto).toList();
        return ResponseEntity.ok(list);
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
        WebMvcLinkBuilder link2 = linkTo(methodOn(this.getClass()).findAll());
        resource.add(link1.withRel("classroom-self-info"));
        resource.add(link2.withRel("classroom-all-info"));

        return resource;
    }

    private ClassroomDTO convertToDto(Classroom obj) {
        return modelMapper.map(obj, ClassroomDTO.class);
    }

    private Classroom convertToEntity(ClassroomDTO dto) {
        return modelMapper.map(dto, Classroom.class);
    }
}
