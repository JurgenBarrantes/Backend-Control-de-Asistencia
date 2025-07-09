package com.systems.controller;

import java.net.URI;
import java.util.List;

import org.springframework.data.domain.Page;
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

import com.systems.dto.CustomPageResponse;
import com.systems.dto.SubjectDTO;
import com.systems.model.Subject;
import com.systems.service.ISubjectService;

import lombok.RequiredArgsConstructor;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/subjects")
@RequiredArgsConstructor
// @CrossOrigin(origins = "*")
public class SubjectController { // es para manejar las solicitudes relacionadas con las asignaturas
	private final ISubjectService service;

	// @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
	@GetMapping
	public ResponseEntity<?> findAll(
			@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer size,
			@RequestParam(required = false) String sortBy,
			@RequestParam(required = false) String sortDirection) throws Exception {

		// Si se proporcionan parámetros de paginación, usar paginación
		if (page != null || size != null) {
			int userPageNumber = page != null ? page : 1; // Frontend usa base-1
			int pageSize = size != null ? size : 10;
			String sortField = sortBy != null ? sortBy : "idSubject";
			String sortDir = sortDirection != null ? sortDirection : "asc";

			// Convertir de base-1 (frontend) a base-0 (Spring)
			int springPageNumber = Math.max(0, userPageNumber - 1);

			Page<Subject> entityPage = service.findAllPaginated(springPageNumber, pageSize, sortField, sortDir);
			Page<SubjectDTO> dtoPage = entityPage.map(this::convertToDto);

			// Usar CustomPageResponse para manejar la paginación de forma consistente
			return ResponseEntity.ok(new CustomPageResponse<>(dtoPage, userPageNumber));
		} else {
			// Sin parámetros de paginación, devolver lista completa
			List<SubjectDTO> list = service.findAll().stream().map(this::convertToDto).toList();
			return ResponseEntity.ok(list);
		}
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
		WebMvcLinkBuilder link2 = linkTo(methodOn(this.getClass()).findAll(null, null, null, null));
		resource.add(link1.withRel("subject-self-info"));
		resource.add(link2.withRel("subject-all-info"));

		return resource;
	}

	private SubjectDTO convertToDto(Subject obj) {
		if (obj == null) {
			return null;
		}
		SubjectDTO dto = new SubjectDTO();
		dto.setIdSubject(obj.getIdSubject());
		dto.setName(obj.getName());
		return dto;
	}

	private Subject convertToEntity(SubjectDTO dto) {
		if (dto == null) {
			return null;
		}
		Subject entity = new Subject();
		entity.setIdSubject(dto.getIdSubject());
		entity.setName(dto.getName());
		return entity;
	}
}
