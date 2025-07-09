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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.systems.dto.PersonDTO;
import com.systems.dto.UserResponseDTO;
import com.systems.dto.RoleDTO;
import com.systems.model.Person;
import com.systems.model.User;
import com.systems.service.IPersonService;

import lombok.RequiredArgsConstructor;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/persons")
@RequiredArgsConstructor
// @CrossOrigin(origins = "*")
public class PersonController { // es para manejar las solicitudes relacionadas con las personas (estudiantes,
								// profesores, etc.)
	private final IPersonService service;

	// @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
	@GetMapping
	public ResponseEntity<?> findAll(
			@RequestParam(value = "search", required = false) String search,
			@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer size,
			@RequestParam(required = false) String sortBy,
			@RequestParam(required = false) String sortDirection) throws Exception {

		// Si se proporcionan parámetros de paginación, usar paginación
		if (page != null || size != null) {
			int pageNumber = page != null ? page : 0;
			int pageSize = size != null ? size : 10;
			String sortField = sortBy != null ? sortBy : "idPerson";
			String sortDir = sortDirection != null ? sortDirection : "asc";

			// Para paginación, usar el método que carga User
			Page<Person> entityPage = service.findAllWithUser(
					org.springframework.data.domain.PageRequest.of(pageNumber, pageSize,
							sortDir.equalsIgnoreCase("desc")
									? org.springframework.data.domain.Sort.by(sortField).descending()
									: org.springframework.data.domain.Sort.by(sortField).ascending()));
			Page<PersonDTO> dtoPage = entityPage.map(this::convertToDto);
			System.out.println("=== PAGINATED PERSONS ===");
			System.out.println("Found " + dtoPage.getTotalElements() + " total elements, page " + pageNumber + " of "
					+ dtoPage.getTotalPages());
			return ResponseEntity.ok(dtoPage);
		} else {
			// Sin paginación, devolver lista completa con filtrado
			List<PersonDTO> list;

			if ("teachers".equalsIgnoreCase(search)) {
				list = service.findPersonsWhoAreTeachers().stream().map(this::convertToDto).toList();
				System.out.println("=== FILTERING TEACHERS ===");
				System.out.println("Found " + list.size() + " teachers");
			} else if ("students".equalsIgnoreCase(search)) {
				list = service.findPersonsWhoAreStudents().stream().map(this::convertToDto).toList();
				System.out.println("=== FILTERING STUDENTS ===");
				System.out.println("Found " + list.size() + " students");
			} else {
				list = service.findAllWithUser().stream().map(this::convertToDto).toList();
				System.out.println("=== NO FILTER - ALL PERSONS ===");
				System.out.println("Found " + list.size() + " persons");
			}

			return ResponseEntity.ok(list);
		}
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
		Person entity = convertToEntity(dto);
		Person obj = service.save(entity);
		// return ResponseEntity.ok(obj);

		// location: http://localhost:9091/persons/{id}
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
		WebMvcLinkBuilder link2 = linkTo(methodOn(this.getClass()).findAll(null, null, null, null, null));
		resource.add(link1.withRel("publisher-self-info"));
		resource.add(link2.withRel("publisher-all-info"));

		return resource;
	}

	private PersonDTO convertToDto(Person obj) {
		PersonDTO dto = new PersonDTO();
		dto.setIdPerson(obj.getIdPerson());
		dto.setDni(obj.getDni());
		dto.setFirstName(obj.getFirstName());
		dto.setLastName(obj.getLastName());

		// Convertir LocalDate a String
		if (obj.getBirthdate() != null) {
			dto.setBirthdate(obj.getBirthdate().toString());
		}

		dto.setGender(obj.getGender());
		dto.setAddress(obj.getAddress());
		dto.setPhone(obj.getPhone());
		dto.setEmail(obj.getEmail());

		// Convertir User completo si existe
		if (obj.getUser() != null) {
			UserResponseDTO userDto = new UserResponseDTO();
			userDto.setIdUser(obj.getUser().getIdUser());
			userDto.setUsername(obj.getUser().getUsername());
			// NO incluir password por seguridad
			userDto.setEnabled(obj.getUser().getEnabled());

			// Convertir roles si existen
			if (obj.getUser().getRoles() != null && !obj.getUser().getRoles().isEmpty()) {
				List<RoleDTO> rolesDto = obj.getUser().getRoles().stream()
						.map(role -> {
							RoleDTO roleDto = new RoleDTO();
							roleDto.setIdRole(role.getIdRole());
							roleDto.setName(role.getName());
							return roleDto;
						}).toList();
				userDto.setRoles(rolesDto);
			}

			dto.setUser(userDto);
		}

		return dto;
	}

	private Person convertToEntity(PersonDTO dto) {
		Person person = new Person();
		person.setIdPerson(dto.getIdPerson());
		person.setDni(dto.getDni());
		person.setFirstName(dto.getFirstName());
		person.setLastName(dto.getLastName());

		// Convertir String a LocalDate - siempre asegurar que tenga un valor
		if (dto.getBirthdate() != null && !dto.getBirthdate().trim().isEmpty()) {
			try {
				person.setBirthdate(java.time.LocalDate.parse(dto.getBirthdate()));
			} catch (Exception e) {
				throw new IllegalArgumentException("Formato de fecha inválido. Use YYYY-MM-DD");
			}
		} else {
			// Si no se proporciona fecha de nacimiento, usar fecha actual como fallback
			person.setBirthdate(java.time.LocalDate.now());
		}

		person.setGender(dto.getGender());
		person.setAddress(dto.getAddress());
		person.setPhone(dto.getPhone());

		// Manejar email - campo obligatorio
		if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
			person.setEmail(dto.getEmail());
		} else {
			// Generar email por defecto si no se proporciona
			person.setEmail(dto.getDni() + "@default.com");
		}

		// Manejar relación con User - opcional
		if (dto.getUser() != null && dto.getUser().getIdUser() != null) {
			User user = new User();
			user.setIdUser(dto.getUser().getIdUser());
			person.setUser(user);
		}

		return person;
	}
}
