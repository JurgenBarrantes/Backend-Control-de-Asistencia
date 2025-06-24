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

import com.systems.dto.SubjectDTO;
import com.systems.model.Subject;
import com.systems.service.ISubjectService;

import lombok.RequiredArgsConstructor;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/subjects")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SubjectController { //es para manejar las solicitudes relacionadas con las asignaturas
    private final ISubjectService service;
	private final ModelMapper modelMapper;

	//@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
	@GetMapping
	public ResponseEntity<List<SubjectDTO>> findAll() throws Exception {

		List<SubjectDTO> list = service.findAll().stream().map(this::convertToDto).toList();

		return ResponseEntity.ok(list);
	}

	@GetMapping("/{id}")
	public ResponseEntity<SubjectDTO> findById(@PathVariable("id") Integer id) throws Exception {
		SubjectDTO obj = convertToDto(service.findById(id));
		return ResponseEntity.ok(obj);
	}

	@PostMapping
	public ResponseEntity<Void> save(@RequestBody SubjectDTO dto) throws Exception {
		Subject obj = service.save(convertToEntity(dto));

		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(obj.getIdSubject()).toUri();
		return ResponseEntity.created(location).build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<SubjectDTO> update(@PathVariable("id") Integer id, @RequestBody SubjectDTO dto)
			throws Exception {
		dto.setIdSubject(id);
		Subject obj = service.update(convertToEntity(dto), id);
		SubjectDTO dto1 = convertToDto(obj);
		return ResponseEntity.ok(dto1);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Integer id)
			throws Exception {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("hateoas/{id}")
	public EntityModel<SubjectDTO> findByIdHateoas(@PathVariable("id") Integer id) throws Exception {
		Subject obj = service.findById(id);
		EntityModel<SubjectDTO> resource = EntityModel.of(convertToDto(obj));

		WebMvcLinkBuilder link1 = linkTo(methodOn(this.getClass()).findById(id));
		WebMvcLinkBuilder link2 = linkTo(methodOn(this.getClass()).findAll());
		resource.add(link1.withRel("subject-self-info"));
		resource.add(link2.withRel("subject-all-info"));

		return resource;
	}

	private SubjectDTO convertToDto(Subject obj) {
		return modelMapper.map(obj, SubjectDTO.class);
	}

	private Subject convertToEntity(SubjectDTO dto) {
		return modelMapper.map(dto, Subject.class);
	}
}
