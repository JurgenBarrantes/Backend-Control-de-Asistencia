package com.systems.controller;

import java.net.URI;
import java.util.List;

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

import com.systems.dto.CustomPageResponse;
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
            String sortField = sortBy != null ? sortBy : "idRole";
            String sortDir = sortDirection != null ? sortDirection : "asc";

            // Convertir de base-1 (frontend) a base-0 (Spring)
            int springPageNumber = Math.max(0, userPageNumber - 1);

            Page<Role> entityPage = service.findAllPaginated(springPageNumber, pageSize, sortField, sortDir);
            Page<RoleDTO> dtoPage = entityPage.map(this::convertToDto);

            // Usar CustomPageResponse para manejar la paginación de forma consistente
            return ResponseEntity.ok(new CustomPageResponse<>(dtoPage, userPageNumber));
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
        // TEMPORAL: Generar ID manualmente hasta que se configure AUTO_INCREMENT en la
        // DB
        if (dto.getIdRole() == null) {
            Integer nextId = getNextRoleId();
            dto.setIdRole(nextId);
        }

        // Crear la entidad
        Role entity = convertToEntity(dto);
        Role obj = service.save(entity);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(obj.getIdRole()).toUri();
        return ResponseEntity.created(location).build();
    }

    // TEMPORAL: Método para generar ID manualmente hasta que se configure
    // AUTO_INCREMENT
    private Integer getNextRoleId() {
        try {
            List<Role> allRoles = service.findAll();
            if (allRoles.isEmpty()) {
                return 1;
            }
            // Obtener el ID máximo y sumar 1
            Integer maxId = allRoles.stream()
                    .mapToInt(Role::getIdRole)
                    .max()
                    .orElse(0);
            return maxId + 1;
        } catch (Exception e) {
            // Si hay error, empezar desde 1
            return 1;
        }
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
        RoleDTO dto = new RoleDTO();
        dto.setIdRole(obj.getIdRole());
        dto.setName(obj.getName());
        dto.setDescription(obj.getDescription());
        return dto;
    }

    private Role convertToEntity(RoleDTO dto) {
        // Validar campos obligatorios
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del rol es obligatorio");
        }
        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción del rol es obligatoria");
        }

        Role role = new Role();

        // Solo establecer el ID si no es null (para operaciones PUT)
        // Para POST, el ID será null y Hibernate lo generará automáticamente
        if (dto.getIdRole() != null) {
            role.setIdRole(dto.getIdRole());
        }
        role.setName(dto.getName().trim());
        role.setDescription(dto.getDescription().trim());

        return role;
    }
}
