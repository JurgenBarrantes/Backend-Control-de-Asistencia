package com.systems.controller;

import java.net.URI;
import java.util.List;

import org.modelmapper.ModelMapper;
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

import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/tardinessrules")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*") 
public class TardinessRuleController { //es para manejar las solicitudes relacionadas con las reglas de tardanza
    private final ITardinessRuleService service;
    private final ModelMapper modelMapper;

    //@PreAuthorize("hasAuthority('ADMIN')or hasAuthority('USER')")
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
            String sortField = sortBy != null ? sortBy : "idTardinessRule";
            String sortDir = sortDirection != null ? sortDirection : "asc";
            
            Page<TardinessRule> entityPage = service.findAllPaginated(pageNumber, pageSize, sortField, sortDir);
            Page<TardinessRuleDTO> dtoPage = entityPage.map(this::convertToDto);
            return ResponseEntity.ok(dtoPage);
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
		return modelMapper.map(obj, TardinessRuleDTO.class);
	}

	private TardinessRule convertToEntity(TardinessRuleDTO dto) {
		return modelMapper.map(dto, TardinessRule.class);
	}
    
}
