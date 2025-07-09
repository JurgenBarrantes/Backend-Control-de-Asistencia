package com.systems.controller;

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

import com.systems.dto.StudentDTO;
import com.systems.model.Person;
import com.systems.model.Student;
import com.systems.service.IPersonService;
import com.systems.service.IStudentService;

import lombok.RequiredArgsConstructor;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class StudentController { //es para manejar las solicitudes relacionadas con los estudiantes
    private final IStudentService service;
    private final IPersonService personService;
	//private final ModelMapper modelMapper;

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
			String sortField = sortBy != null ? sortBy : "idStudent";
			String sortDir = sortDirection != null ? sortDirection : "asc";
			
			Page<Student> entityPage = service.findAllPaginated(pageNumber, pageSize, sortField, sortDir);
			Page<StudentDTO> dtoPage = entityPage.map(this::convertToDto);
			return ResponseEntity.ok(dtoPage);
		} else {
			// Sin parámetros de paginación, devolver lista completa
			List<StudentDTO> list = service.findAll().stream().map(this::convertToDto).toList();
			return ResponseEntity.ok(list);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<StudentDTO> findById(@PathVariable("id") Integer id) throws Exception {
		StudentDTO obj = convertToDto(service.findById(id));
		return ResponseEntity.ok(obj);
	}

	@PostMapping
	public ResponseEntity<Void> save(@RequestBody StudentDTO dto) throws Exception {
		Student obj = service.save(convertToEntity(dto));

		// location: http://localhost:9091/students/{id}
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(obj.getIdStudent()).toUri();
		return ResponseEntity.created(location).build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<StudentDTO> update(@PathVariable("id") Integer id, @RequestBody StudentDTO dto)
			throws Exception {
		dto.setIdStudent(id);
		Student obj = service.update(convertToEntityForUpdate(dto, id), id);
		StudentDTO dto1 = convertToDto(obj);
		return ResponseEntity.ok(dto1);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Integer id)
			throws Exception {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("hateoas/{id}")
	public EntityModel<StudentDTO> findByIdHateoas(@PathVariable("id") Integer id) throws Exception {
		Student obj = service.findById(id);
		EntityModel<StudentDTO> resource = EntityModel.of(convertToDto(obj));

		// Generar links informativos
		// localhost:9090/students/5
		WebMvcLinkBuilder link1 = linkTo(methodOn(this.getClass()).findById(id));
		WebMvcLinkBuilder link2 = linkTo(methodOn(this.getClass()).findAll(null, null, null, null));
		resource.add(link1.withRel("student-self-info"));
		resource.add(link2.withRel("student-all-info"));

		return resource;
	}

	private StudentDTO convertToDto(Student obj) {
		StudentDTO dto = new StudentDTO();
		dto.setIdStudent(obj.getIdStudent());
		
		// Debug logging
		System.out.println("=== STUDENT CONVERSION DEBUG ===");
		System.out.println("Student ID: " + obj.getIdStudent());
		System.out.println("Person is null: " + (obj.getPerson() == null));
		
		// Mapear información de la persona asociada
		if (obj.getPerson() != null) {
			System.out.println("Person found - FirstName: " + obj.getPerson().getFirstName());
			dto.setFirstName(obj.getPerson().getFirstName());
			dto.setLastName(obj.getPerson().getLastName());
			dto.setFullName(obj.getPerson().getFirstName() + " " + obj.getPerson().getLastName());
			dto.setDni(obj.getPerson().getDni());
			dto.setEmail(obj.getPerson().getEmail());
			dto.setPhone(obj.getPerson().getPhone());
			dto.setAddress(obj.getPerson().getAddress());
			dto.setGender(obj.getPerson().getGender());
			dto.setBirthdate(obj.getPerson().getBirthdate() != null ? obj.getPerson().getBirthdate().toString() : null);
		} else {
			System.out.println("Person is NULL for student: " + obj.getIdStudent());
		}
		
		return dto;
	}

	private Student convertToEntity(StudentDTO dto) {
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
			
			// Crear la entidad Student
			Student student = new Student();
			student.setIdStudent(dto.getIdStudent()); // null para POST, ID para PUT
			student.setPerson(savedPerson);
			
			return student;
		} catch (Exception e) {
			System.err.println("Error creating Student entity: " + e.getMessage());
			throw new RuntimeException("Error al crear el estudiante: " + e.getMessage(), e);
		}
	}
	
	private Student convertToEntityForUpdate(StudentDTO dto, Integer studentId) {
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
			// Buscar el Student existente
			Student existingStudent = service.findById(studentId);
			
			// Actualizar la Person asociada
			Person person = existingStudent.getPerson();
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
			
			// Actualizar el Student
			existingStudent.setPerson(updatedPerson);
			
			return existingStudent;
		} catch (Exception e) {
			System.err.println("Error updating Student entity: " + e.getMessage());
			throw new RuntimeException("Error al actualizar el estudiante: " + e.getMessage(), e);
		}
	}
}
