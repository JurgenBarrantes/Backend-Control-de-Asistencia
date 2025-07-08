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
import com.systems.model.Student;
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
		Student obj = service.update(convertToEntity(dto), id);
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
		} else {
			System.out.println("Person is NULL for student: " + obj.getIdStudent());
		}
		
		return dto;
	}

	private Student convertToEntity(StudentDTO dto) {
		Student entity = new Student();
		entity.setIdStudent(dto.getIdStudent());
		
		// Para operaciones POST/PUT, necesitarías manejar la relación con Person
		// Por ahora, usar el mapeo básico para no romper funcionalidad existente
		// En un escenario real, necesitarías buscar la Person por ID o crear/actualizar
		
		return entity;
	}
}
