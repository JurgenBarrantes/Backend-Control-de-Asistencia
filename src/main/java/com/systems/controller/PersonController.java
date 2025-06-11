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

import com.systems.dto.PersonDTO;
import com.systems.model.Person;
import com.systems.service.IPersonService;

import lombok.RequiredArgsConstructor;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/persons")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PersonController {
    private final IPersonService service;
	private final ModelMapper modelMapper;

	//@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
	@GetMapping
	public ResponseEntity<List<PersonDTO>> findAll() throws Exception {
		// ModelMapper modelMapper = new ModelMapper();
		// return service.findAll();
		// List<PublisherDTO> list = service.findAll().stream().map(e -> new
		// PublisherDTO(e.getIdPublisher(), e.getName(), e.getAddress())).toList();
		// List<PublisherDTO> list = service.findAll().stream().map(e ->
		// modelMapper.map(e, PublisherDTO.class)).toList();

		List<PersonDTO> list = service.findAll().stream().map(this::convertToDto).toList();

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
		WebMvcLinkBuilder link2 = linkTo(methodOn(this.getClass()).findAll());
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
