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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.systems.dto.PersonDTO;
import com.systems.model.Person;
import com.systems.service.IPersonService;

import lombok.RequiredArgsConstructor;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/persons")
@RequiredArgsConstructor
public class PersonController { // es para manejar las solicitudes relacionadas con las personas (estudiantes,
								// profesores, etc.)
	private final IPersonService service;
	private final ModelMapper modelMapper;

	// @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
	@GetMapping
	public ResponseEntity<List<PersonDTO>> findAll(@RequestParam(value = "search", required = false) String search)
			throws Exception {

		List<PersonDTO> list;

		// Filtrar según el parámetro search
		if ("teachers".equalsIgnoreCase(search)) {
			// Obtener solo personas que son teachers
			list = service.findPersonsWhoAreTeachers().stream().map(this::convertToDto).toList();
			System.out.println("=== FILTERING TEACHERS ===");
			System.out.println("Found " + list.size() + " teachers");
		} else if ("students".equalsIgnoreCase(search)) {
			// Obtener solo personas que son students
			list = service.findPersonsWhoAreStudents().stream().map(this::convertToDto).toList();
			System.out.println("=== FILTERING STUDENTS ===");
			System.out.println("Found " + list.size() + " students");
		} else {
			// Sin filtro, obtener todas las personas
			list = service.findAll().stream().map(this::convertToDto).toList();
			System.out.println("=== NO FILTER - ALL PERSONS ===");
			System.out.println("Found " + list.size() + " persons");
		}

		return ResponseEntity.ok(list);
	}

	@GetMapping("/{id}")
	public ResponseEntity<PersonDTO> findById(@PathVariable("id") Integer id) throws Exception {
		// Publisher obj = service.findById(id);
		// PublisherDTO obj = modelMapper.map(service.findById(id), PublisherDTO.class);
		PersonDTO obj = convertToDto(service.findById(id));
		return ResponseEntity.ok(obj);
	}

	@PostMapping
	public ResponseEntity<Void> save(@RequestBody PersonDTO dto) throws Exception {
		Person obj = service.save(convertToEntity(dto));
		// return ResponseEntity.ok(obj);

		// location: http://localhost:9090/persons/{id}
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(obj.getIdPerson()).toUri();
		return ResponseEntity.created(location).build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<PersonDTO> update(@PathVariable("id") Integer id, @RequestBody PersonDTO dto)
			throws Exception {
		// Publisher obj = service.update(modelMapper.map(dto,Publisher.class), id);
		// PublisherDTO dto1 = modelMapper.map(obj, PublisherDTO.class);
		// return ResponseEntity.ok(dto1);
		dto.setIdPerson(id);
		Person obj = service.update(convertToEntity(dto), id);
		PersonDTO dto1 = convertToDto(obj);
		return ResponseEntity.ok(dto1);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Integer id)
			throws Exception {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("hateoas/{id}")
	public EntityModel<PersonDTO> findByIdHateoas(@PathVariable("id") Integer id) throws Exception {
		Person obj = service.findById(id);
		EntityModel<PersonDTO> resource = EntityModel.of(convertToDto(obj));

		// Generar links informativos
		// localhost:9090/publishers/5
		WebMvcLinkBuilder link1 = linkTo(methodOn(this.getClass()).findById(id));
		WebMvcLinkBuilder link2 = linkTo(methodOn(this.getClass()).findAll(null));
		resource.add(link1.withRel("publisher-self-info"));
		resource.add(link2.withRel("publisher-all-info"));

		return resource;
	}

	private PersonDTO convertToDto(Person obj) {
		return modelMapper.map(obj, PersonDTO.class);
	}

	private Person convertToEntity(PersonDTO dto) {
		return modelMapper.map(dto, Person.class);
	}
}
