package com.systems.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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

import com.systems.dto.PersonDTO;
import com.systems.dto.RoleDTO;
import com.systems.dto.UserDTO;
import com.systems.model.Person;
import com.systems.model.Role;
import com.systems.model.User;
import com.systems.service.IPersonService;
import com.systems.service.IRoleService;
import com.systems.service.IUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
// @CrossOrigin(origins = "*")
public class UserController { // es para manejar las solicitudes relacionadas con los usuarios
	private final IUserService service;
	private final IRoleService roleService;
	private final IPersonService personService;

	// @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
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
			String sortField = sortBy != null ? sortBy : "idUser";
			String sortDir = sortDirection != null ? sortDirection : "asc";

			Page<User> entityPage = service.findAllPaginated(pageNumber, pageSize, sortField, sortDir);
			Page<UserDTO> dtoPage = entityPage.map(this::convertToDto);
			return ResponseEntity.ok(dtoPage);
		} else {
			// Sin parámetros de paginación, devolver lista completa
			List<UserDTO> list = service.findAll().stream().map(this::convertToDto).toList();
			return ResponseEntity.ok(list);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserDTO> findById(@PathVariable("id") Integer id) throws Exception {
		UserDTO obj = convertToDto(service.findById(id));
		return ResponseEntity.ok(obj);
	}

	@PostMapping
	public ResponseEntity<Void> save(@RequestBody UserDTO dto) throws Exception {
		User obj = service.save(convertToEntity(dto));

		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(obj.getIdUser()).toUri();
		return ResponseEntity.created(location).build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserDTO> update(@PathVariable("id") Integer id, @RequestBody UserDTO dto)
			throws Exception {
		dto.setIdUser(id);
		User obj = service.update(convertToEntity(dto), id);
		UserDTO dto1 = convertToDto(obj);
		return ResponseEntity.ok(dto1);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Integer id)
			throws Exception {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("hateoas/{id}")
	public EntityModel<UserDTO> findByIdHateoas(@PathVariable("id") Integer id) throws Exception {
		User obj = service.findById(id);
		EntityModel<UserDTO> resource = EntityModel.of(convertToDto(obj));

		WebMvcLinkBuilder link1 = linkTo(methodOn(this.getClass()).findById(id));
		WebMvcLinkBuilder link2 = linkTo(methodOn(this.getClass()).findAll(null, null, null, null));
		resource.add(link1.withRel("user-self-info"));
		resource.add(link2.withRel("user-all-info"));

		return resource;
	}

	private UserDTO convertToDto(User obj) {
		UserDTO dto = new UserDTO();
		dto.setIdUser(obj.getIdUser());
		dto.setUsername(obj.getUsername());
		// No devolver la contraseña por seguridad
		dto.setPassword(null);
		dto.setEnabled(obj.getEnabled());

		// Convertir roles si existen
		if (obj.getRoles() != null && !obj.getRoles().isEmpty()) {
			List<RoleDTO> roleDTOs = obj.getRoles().stream()
					.map(role -> {
						RoleDTO roleDTO = new RoleDTO();
						roleDTO.setIdRole(role.getIdRole());
						roleDTO.setName(role.getName());
						roleDTO.setDescription(role.getDescription());
						return roleDTO;
					})
					.toList();
			dto.setRoles(roleDTOs);
		} else {
			dto.setRoles(null);
		}

		// Convertir person si existe
		if (obj.getPerson() != null) {
			PersonDTO personDTO = new PersonDTO();
			personDTO.setIdPerson(obj.getPerson().getIdPerson());
			personDTO.setDni(obj.getPerson().getDni());
			personDTO.setFirstName(obj.getPerson().getFirstName());
			personDTO.setLastName(obj.getPerson().getLastName());
			personDTO.setBirthdate(obj.getPerson().getBirthdate().toString());
			personDTO.setGender(obj.getPerson().getGender());
			personDTO.setAddress(obj.getPerson().getAddress());
			personDTO.setPhone(obj.getPerson().getPhone());
			personDTO.setEmail(obj.getPerson().getEmail());
			// No establecer user aquí para evitar recursión infinita
			personDTO.setUser(null);
			dto.setPerson(personDTO);
		} else {
			dto.setPerson(null);
		}

		return dto;
	}

	private User convertToEntity(UserDTO dto) {
		// Validar campos obligatorios
		if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
			throw new IllegalArgumentException("El nombre de usuario es obligatorio");
		}
		if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
			throw new IllegalArgumentException("La contraseña es obligatoria");
		}

		User user = new User();
		user.setIdUser(dto.getIdUser()); // null para POST, ID para PUT
		user.setUsername(dto.getUsername().trim());
		user.setPassword(dto.getPassword()); // En un sistema real, debería estar encriptada

		// Si no se especifica enabled, por defecto true
		user.setEnabled(dto.getEnabled() != null ? dto.getEnabled() : true);

		// Manejar person si se proporciona
		if (dto.getPerson() != null) {
			PersonDTO personDTO = dto.getPerson();
			Person person = null;

			// Si es una actualización y ya existe una person con ID
			if (personDTO.getIdPerson() != null) {
				try {
					person = personService.findById(personDTO.getIdPerson());
					// Actualizar campos de la persona existente
					person.setDni(personDTO.getDni());
					person.setFirstName(personDTO.getFirstName());
					person.setLastName(personDTO.getLastName());
					if (personDTO.getBirthdate() != null) {
						person.setBirthdate(java.time.LocalDate.parse(personDTO.getBirthdate()));
					}
					person.setGender(personDTO.getGender());
					person.setAddress(personDTO.getAddress());
					person.setPhone(personDTO.getPhone());
					person.setEmail(personDTO.getEmail());
					person = personService.update(person, person.getIdPerson());
				} catch (Exception e) {
					throw new IllegalArgumentException(
							"Error al actualizar la persona con ID: " + personDTO.getIdPerson());
				}
			} else {
				// Crear nueva persona
				person = new Person();
				person.setDni(personDTO.getDni());
				person.setFirstName(personDTO.getFirstName());
				person.setLastName(personDTO.getLastName());
				if (personDTO.getBirthdate() != null) {
					person.setBirthdate(java.time.LocalDate.parse(personDTO.getBirthdate()));
				}
				person.setGender(personDTO.getGender());
				person.setAddress(personDTO.getAddress());
				person.setPhone(personDTO.getPhone());
				person.setEmail(personDTO.getEmail());
				// La relación con User se establecerá después de guardar el User
				person.setUser(user);

				try {
					person = personService.save(person);
				} catch (Exception e) {
					throw new IllegalArgumentException("Error al crear la persona: " + e.getMessage());
				}
			}

			user.setPerson(person);
		}

		// Manejar roles si se proporcionan
		if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
			List<Role> roles = dto.getRoles().stream()
					.map(roleDTO -> {
						// Si solo se proporciona el nombre del rol, buscar en la base de datos
						if (roleDTO.getName() != null
								&& (roleDTO.getIdRole() == null || roleDTO.getDescription() == null)) {
							return roleService.findByName(roleDTO.getName())
									.orElseThrow(() -> new IllegalArgumentException(
											"No se encontró el rol con nombre: " + roleDTO.getName()));
						} else {
							// Si se proporcionan todos los campos, crear el rol directamente
							Role role = new Role();
							role.setIdRole(roleDTO.getIdRole());
							role.setName(roleDTO.getName());
							role.setDescription(roleDTO.getDescription());
							return role;
						}
					})
					.toList();
			user.setRoles(roles);
		} else {
			user.setRoles(null);
		}

		return user;
	}
}
