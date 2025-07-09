package com.systems.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;

//import org.modelmapper.ModelMapper;
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

import com.systems.dto.TeacherDTO;
import com.systems.dto.CustomPageResponse;
import com.systems.model.Person;
import com.systems.model.Teacher;
import com.systems.service.IPersonService;
import com.systems.service.ITeacherService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
// @CrossOrigin(origins = "*")
public class TeacherController { // es para manejar las solicitudes relacionadas con los profesores
	private final ITeacherService service;
	private final IPersonService personService;
	// private final ModelMapper modelMapper;

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
			String sortField = sortBy != null ? sortBy : "idTeacher";
			String sortDir = sortDirection != null ? sortDirection : "asc";

			// Convertir de base-1 (frontend) a base-0 (Spring)
			int springPageNumber = Math.max(0, userPageNumber - 1);

			Page<Teacher> entityPage = service.findAllPaginated(springPageNumber, pageSize, sortField, sortDir);
			Page<TeacherDTO> dtoPage = entityPage.map(this::convertToDto);

			// Usar CustomPageResponse para manejar la paginación de forma consistente
			return ResponseEntity.ok(new CustomPageResponse<>(dtoPage, userPageNumber));
		} else {
			// Sin parámetros de paginación, devolver lista completa
			List<TeacherDTO> list = service.findAll().stream().map(this::convertToDto).toList();
			return ResponseEntity.ok(list);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<TeacherDTO> findById(@PathVariable("id") Integer id) throws Exception {

		TeacherDTO obj = convertToDto(service.findById(id));
		return ResponseEntity.ok(obj);
	}

	@PostMapping
	public ResponseEntity<Void> save(@RequestBody TeacherDTO dto) throws Exception {

		Teacher obj = service.save(convertToEntity(dto));

		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(obj.getIdTeacher()).toUri();
		return ResponseEntity.created(location).build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<TeacherDTO> update(@PathVariable("id") Integer id, @RequestBody TeacherDTO dto)
			throws Exception {
		dto.setIdTeacher(id);
		Teacher obj = service.update(convertToEntityForUpdate(dto, id), id);
		TeacherDTO dto1 = convertToDto(obj);
		return ResponseEntity.ok(dto1);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Integer id)
			throws Exception {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("hateoas/{id}")
	public EntityModel<TeacherDTO> findByIdHateoas(@PathVariable("id") Integer id) throws Exception {
		Teacher obj = service.findById(id);
		EntityModel<TeacherDTO> resource = EntityModel.of(convertToDto(obj));

		WebMvcLinkBuilder link1 = linkTo(methodOn(this.getClass()).findById(id));
		WebMvcLinkBuilder link2 = linkTo(methodOn(this.getClass()).findAll(null, null, null, null));
		resource.add(link1.withRel("teacher-self-info"));
		resource.add(link2.withRel("teacher-all-info"));

		return resource;
	}

	private TeacherDTO convertToDto(Teacher obj) {
		TeacherDTO dto = new TeacherDTO();
		dto.setIdTeacher(obj.getIdTeacher());

		// Mapear información de la persona asociada
		if (obj.getPerson() != null) {
			dto.setFirstName(obj.getPerson().getFirstName());
			dto.setLastName(obj.getPerson().getLastName());
			dto.setFullName(obj.getPerson().getFirstName() + " " + obj.getPerson().getLastName());
			dto.setDni(obj.getPerson().getDni());
			dto.setEmail(obj.getPerson().getEmail());
			dto.setPhone(obj.getPerson().getPhone());
			dto.setAddress(obj.getPerson().getAddress());
			dto.setGender(obj.getPerson().getGender());
			dto.setBirthdate(obj.getPerson().getBirthdate() != null ? obj.getPerson().getBirthdate().toString() : null);
		}

		return dto;
	}

	private Teacher convertToEntity(TeacherDTO dto) {
		// Validar campos obligatorios
		if (dto.getFirstName() == null || dto.getFirstName().trim().isEmpty()) {
			throw new IllegalArgumentException("El nombre es obligatorio");
		}
		if (dto.getLastName() == null || dto.getLastName().trim().isEmpty()) {
			throw new IllegalArgumentException("El apellido es obligatorio");
		}
		if (dto.getDni() == null || dto.getDni().trim().isEmpty()) {
			throw new IllegalArgumentException("El DNI es obligatorio");
		}
		if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
			throw new IllegalArgumentException("El email es obligatorio");
		}
		if (dto.getBirthdate() == null || dto.getBirthdate().trim().isEmpty()) {
			throw new IllegalArgumentException("La fecha de nacimiento es obligatoria");
		}
		if (dto.getGender() == null || dto.getGender().trim().isEmpty()) {
			throw new IllegalArgumentException("El género es obligatorio");
		}
		if (dto.getAddress() == null || dto.getAddress().trim().isEmpty()) {
			throw new IllegalArgumentException("La dirección es obligatoria");
		}

		try {
			// Crear la entidad Person primero
			Person person = new Person();
			person.setFirstName(dto.getFirstName().trim());
			person.setLastName(dto.getLastName().trim());
			person.setDni(dto.getDni().trim());
			person.setEmail(dto.getEmail().trim());
			person.setPhone(dto.getPhone() != null ? dto.getPhone().trim() : null);
			person.setAddress(dto.getAddress().trim());
			person.setGender(dto.getGender().trim().toUpperCase());

			// Parsear fecha de nacimiento
			try {
				person.setBirthdate(java.time.LocalDate.parse(dto.getBirthdate()));
			} catch (Exception e) {
				throw new IllegalArgumentException("Formato de fecha inválido. Use YYYY-MM-DD");
			}

			// user es opcional, establecer como null
			person.setUser(null);

			// Guardar la Person primero para obtener el ID
			Person savedPerson = personService.save(person);
			System.out.println("Person saved with ID: " + savedPerson.getIdPerson());

			// Crear la entidad Teacher
			Teacher teacher = new Teacher();
			teacher.setIdTeacher(dto.getIdTeacher()); // null para POST, ID para PUT
			teacher.setPerson(savedPerson);

			return teacher;
		} catch (Exception e) {
			System.err.println("Error creating Teacher entity: " + e.getMessage());
			throw new RuntimeException("Error al crear el profesor: " + e.getMessage(), e);
		}
	}

	private Teacher convertToEntityForUpdate(TeacherDTO dto, Integer teacherId) {
		// Validar campos obligatorios
		if (dto.getFirstName() == null || dto.getFirstName().trim().isEmpty()) {
			throw new IllegalArgumentException("El nombre es obligatorio");
		}
		if (dto.getLastName() == null || dto.getLastName().trim().isEmpty()) {
			throw new IllegalArgumentException("El apellido es obligatorio");
		}
		if (dto.getDni() == null || dto.getDni().trim().isEmpty()) {
			throw new IllegalArgumentException("El DNI es obligatorio");
		}
		if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
			throw new IllegalArgumentException("El email es obligatorio");
		}
		if (dto.getBirthdate() == null || dto.getBirthdate().trim().isEmpty()) {
			throw new IllegalArgumentException("La fecha de nacimiento es obligatoria");
		}
		if (dto.getGender() == null || dto.getGender().trim().isEmpty()) {
			throw new IllegalArgumentException("El género es obligatorio");
		}
		if (dto.getAddress() == null || dto.getAddress().trim().isEmpty()) {
			throw new IllegalArgumentException("La dirección es obligatoria");
		}

		try {
			// Buscar el Teacher existente
			Teacher existingTeacher = service.findById(teacherId);

			// Actualizar la Person asociada
			Person person = existingTeacher.getPerson();
			person.setFirstName(dto.getFirstName().trim());
			person.setLastName(dto.getLastName().trim());
			person.setDni(dto.getDni().trim());
			person.setEmail(dto.getEmail().trim());
			person.setPhone(dto.getPhone() != null ? dto.getPhone().trim() : null);
			person.setAddress(dto.getAddress().trim());
			person.setGender(dto.getGender().trim().toUpperCase());

			// Parsear fecha de nacimiento
			try {
				person.setBirthdate(java.time.LocalDate.parse(dto.getBirthdate()));
			} catch (Exception e) {
				throw new IllegalArgumentException("Formato de fecha inválido. Use YYYY-MM-DD");
			}

			// Actualizar la Person
			Person updatedPerson = personService.update(person, person.getIdPerson());

			// Actualizar el Teacher
			existingTeacher.setPerson(updatedPerson);

			return existingTeacher;
		} catch (Exception e) {
			System.err.println("Error updating Teacher entity: " + e.getMessage());
			throw new RuntimeException("Error al actualizar el profesor: " + e.getMessage(), e);
		}
	}
}
