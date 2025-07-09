package com.systems.controller;

import java.net.URI;
import java.util.List;

import com.systems.dto.TardinessRuleDTO;
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

import com.systems.model.TardinessRule;
import com.systems.service.ITardinessRuleService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/tardinessrules")
@RequiredArgsConstructor
// @CrossOrigin(origins = "*")
public class TardinessRuleController { // es para manejar las solicitudes relacionadas con las reglas de tardanza
	private final ITardinessRuleService service;

	// @PreAuthorize("hasAuthority('ADMIN')or hasAuthority('USER')")
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
			String sortField = sortBy != null ? sortBy : "idTardinessRule";
			String sortDir = sortDirection != null ? sortDirection : "asc";

			// Convertir de base-1 (frontend) a base-0 (Spring)
			int springPageNumber = Math.max(0, userPageNumber - 1);

			Page<TardinessRule> entityPage = service.findAllPaginated(springPageNumber, pageSize, sortField, sortDir);
			Page<TardinessRuleDTO> dtoPage = entityPage.map(this::convertToDto);

			// Usar CustomPageResponse para manejar la paginación de forma consistente
			return ResponseEntity.ok(new CustomPageResponse<>(dtoPage, userPageNumber));
		} else {
			// Sin parámetros de paginación, devolver lista completa
			List<TardinessRuleDTO> list = service.findAll().stream().map(this::convertToDto).toList();
			return ResponseEntity.ok(list);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<TardinessRuleDTO> findById(@PathVariable("id") Integer id) throws Exception {
		TardinessRuleDTO obj = convertToDto(service.findById(id));
		return ResponseEntity.ok(obj);
	}

	@PostMapping
	public ResponseEntity<Void> save(@RequestBody TardinessRuleDTO dto) throws Exception {
		TardinessRule obj = service.save(convertToEntity(dto));

		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(obj.getIdTardinessRule()).toUri();
		return ResponseEntity.created(location).build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<TardinessRuleDTO> update(@PathVariable("id") Integer id, @RequestBody TardinessRuleDTO dto)
			throws Exception {
		dto.setIdTardinessRule(id);
		TardinessRule obj = service.update(convertToEntity(dto), id);
		TardinessRuleDTO dto1 = convertToDto(obj);
		return ResponseEntity.ok(dto1);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Integer id)
			throws Exception {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("hateoas/{id}")
	public EntityModel<TardinessRuleDTO> findByIdHateoas(@PathVariable("id") Integer id) throws Exception {
		TardinessRule obj = service.findById(id);
		EntityModel<TardinessRuleDTO> resource = EntityModel.of(convertToDto(obj));

		// Generar links informativos
		WebMvcLinkBuilder link1 = linkTo(methodOn(this.getClass()).findById(id));
		WebMvcLinkBuilder link2 = linkTo(methodOn(this.getClass()).findAll(null, null, null, null));
		resource.add(link1.withRel("tardinessRule-self-info"));
		resource.add(link2.withRel("tardinessRule-all-info"));

		return resource;
	}

	private TardinessRuleDTO convertToDto(TardinessRule obj) {
		if (obj == null) {
			return null;
		}
		TardinessRuleDTO dto = new TardinessRuleDTO();
		dto.setIdTardinessRule(obj.getIdTardinessRule());
		dto.setTardinnessThresholdMinutes(obj.getTardinnessThresholdMinutes());
		dto.setAbsenceThresholdMinutes(obj.getAbsenceThresholdMinutes());
		return dto;
	}

	private TardinessRule convertToEntity(TardinessRuleDTO dto) {
		if (dto == null) {
			return null;
		}
		TardinessRule entity = new TardinessRule();
		entity.setIdTardinessRule(dto.getIdTardinessRule());
		entity.setTardinnessThresholdMinutes(dto.getTardinnessThresholdMinutes());
		entity.setAbsenceThresholdMinutes(dto.getAbsenceThresholdMinutes());
		return entity;
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
