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

import com.systems.dto.RoleDTO;
import com.systems.model.Role;
import com.systems.service.IRoleService;

import lombok.RequiredArgsConstructor;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {
    private final IRoleService service;
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
            String sortField = sortBy != null ? sortBy : "idRole";
            String sortDir = sortDirection != null ? sortDirection : "asc";
            
            Page<Role> entityPage = service.findAllPaginated(pageNumber, pageSize, sortField, sortDir);
            Page<RoleDTO> dtoPage = entityPage.map(this::convertToDto);
            return ResponseEntity.ok(dtoPage);
        } else {
            // Sin parámetros de paginación, devolver lista completa
            List<RoleDTO> list = service.findAll().stream().map(this::convertToDto).toList();
            return ResponseEntity.ok(list);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> findById(@PathVariable("id") Integer id) throws Exception {
        RoleDTO obj = convertToDto(service.findById(id));
        return ResponseEntity.ok(obj);
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody RoleDTO dto) throws Exception {
        Role obj = service.save(convertToEntity(dto));

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(obj.getIdRole()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleDTO> update(@PathVariable("id") Integer id, @RequestBody RoleDTO dto)
            throws Exception {
        dto.setIdRole(id);
        Role obj = service.update(convertToEntity(dto), id);
        RoleDTO dto1 = convertToDto(obj);
        return ResponseEntity.ok(dto1);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) throws Exception {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("hateoas/{id}")
    public EntityModel<RoleDTO> findByIdHateoas(@PathVariable("id") Integer id) throws Exception {
        Role obj = service.findById(id);
        EntityModel<RoleDTO> resource = EntityModel.of(convertToDto(obj));

        WebMvcLinkBuilder link1 = linkTo(methodOn(this.getClass()).findById(id));
        WebMvcLinkBuilder link2 = linkTo(methodOn(this.getClass()).findAll(null, null, null, null));
        resource.add(link1.withRel("role-self-info"));
        resource.add(link2.withRel("role-all-info"));

        return resource;
    }

    private RoleDTO convertToDto(Role obj) {
        return modelMapper.map(obj, RoleDTO.class);
    }

    private Role convertToEntity(RoleDTO dto) {
        return modelMapper.map(dto, Role.class);
    }
}
