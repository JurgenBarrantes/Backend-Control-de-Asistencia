package com.systems.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;

//import org.modelmapper.ModelMapper;
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

import com.systems.dto.TeacherDTO;
import com.systems.model.Teacher;
import com.systems.service.ITeacherService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TeacherController { // es para manejar las solicitudes relacionadas con los profesores
	private final ITeacherService service;
	// private final ModelMapper modelMapper;

	// @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
	@GetMapping
	public ResponseEntity<List<TeacherDTO>> findAll() throws Exception {

		List<TeacherDTO> list = service.findAll().stream().map(this::convertToDto).toList();

		return ResponseEntity.ok(list);
	}

	@GetMapping("/{id}")
	public ResponseEntity<TeacherDTO> findById(@PathVariable("id") Integer id) throws Exception {

		TeacherDTO obj = convertToDto(service.findById(id));
		return ResponseEntity.ok(obj);
	}

	@PostMapping
	public ResponseEntity<Void> save(@RequestBody TeacherDTO dto) throws Exception {

		Teacher obj = service.save(convertToEntity(dto));

		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(obj.getIdTeacher()).toUri();
		return ResponseEntity.created(location).build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<TeacherDTO> update(@PathVariable("id") Integer id, @RequestBody TeacherDTO dto)
			throws Exception {
		dto.setIdTeacher(id);
		Teacher obj = service.update(convertToEntity(dto), id);
		TeacherDTO dto1 = convertToDto(obj);
		return ResponseEntity.ok(dto1);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Integer id)
			throws Exception {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("hateoas/{id}")
	public EntityModel<TeacherDTO> findByIdHateoas(@PathVariable("id") Integer id) throws Exception {
		Teacher obj = service.findById(id);
		EntityModel<TeacherDTO> resource = EntityModel.of(convertToDto(obj));

		WebMvcLinkBuilder link1 = linkTo(methodOn(this.getClass()).findById(id));
		WebMvcLinkBuilder link2 = linkTo(methodOn(this.getClass()).findAll());
		resource.add(link1.withRel("teacher-self-info"));
		resource.add(link2.withRel("teacher-all-info"));

		return resource;
	}

	private TeacherDTO convertToDto(Teacher obj) {
		TeacherDTO dto = new TeacherDTO();
		dto.setIdTeacher(obj.getIdTeacher());

		// Debug logging
		System.out.println("TEACHER CONVERSION DEBUG");
		System.out.println("Teacher ID: " + obj.getIdTeacher());
		System.out.println("Person is null: " + (obj.getPerson() == null));

		// Mapear información de la persona asociada
		if (obj.getPerson() != null) {
			System.out.println("Person found - FirstName: " + obj.getPerson().getFirstName());
			dto.setFirstName(obj.getPerson().getFirstName());
			dto.setLastName(obj.getPerson().getLastName());
			dto.setFullName(obj.getPerson().getFirstName() + " " + obj.getPerson().getLastName());
			dto.setDni(obj.getPerson().getDni());
			dto.setEmail(obj.getPerson().getEmail());
			dto.setPhone(obj.getPerson().getPhone());
		} else {
			System.out.println("Person is NULL for teacher: " + obj.getIdTeacher());
		}

		return dto;
	}

	private Teacher convertToEntity(TeacherDTO dto) {
		Teacher entity = new Teacher();
		entity.setIdTeacher(dto.getIdTeacher());

		// Para operaciones POST/PUT, necesitarías manejar la relación con Person
		// Por ahora, usar el mapeo básico para no romper funcionalidad existente

		return entity;
	}
}
