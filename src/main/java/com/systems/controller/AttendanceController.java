package com.systems.controller;

import java.net.URI;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.systems.dto.AttendanceDTO;
import com.systems.dto.ClassAttendanceDTO;
import com.systems.dto.ClassAttendanceResponseDTO;
import com.systems.model.Attendance;
import com.systems.service.IAttendanceService;

import lombok.RequiredArgsConstructor;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/attendances")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AttendanceController { //es para manejar las solicitudes relacionadas con la asistencia
    private final IAttendanceService service;
	private final ModelMapper modelMapper;

	//@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
	@GetMapping
	public ResponseEntity<List<AttendanceDTO>> findAll() throws Exception { 
		List<AttendanceDTO> list = service.findAll().stream().map(this::convertToDto).toList();

		return ResponseEntity.ok(list);
	}

	@GetMapping("/{id}")
	public ResponseEntity<AttendanceDTO> findById(@PathVariable("id") Integer id) throws Exception {
		AttendanceDTO obj = convertToDto(service.findById(id));
		return ResponseEntity.ok(obj);
	}

	@PostMapping
	public ResponseEntity<Void> save(@RequestBody AttendanceDTO dto) throws Exception {
		Attendance obj = service.save(convertToEntity(dto));

		// location: http://localhost:9090/attendances/{id}
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(obj.getIdAttendance()).toUri();
		return ResponseEntity.created(location).build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<AttendanceDTO> update(@PathVariable("id") Integer id, @RequestBody AttendanceDTO dto)
			throws Exception {
		// Publisher obj = service.update(modelMapper.map(dto,Publisher.class), id);
		// PublisherDTO dto1 = modelMapper.map(obj, PublisherDTO.class);
		// return ResponseEntity.ok(dto1);
		dto.setIdAttendance(id);
		Attendance obj = service.update(convertToEntity(dto), id);
		AttendanceDTO dto1 = convertToDto(obj);
		return ResponseEntity.ok(dto1);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Integer id)
			throws Exception {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("hateoas/{id}")
	public EntityModel<AttendanceDTO> findByIdHateoas(@PathVariable("id") Integer id) throws Exception {
		Attendance obj = service.findById(id);
		EntityModel<AttendanceDTO> resource = EntityModel.of(convertToDto(obj));

		// Generar links informativos
		// localhost:9090/attendances/5
		WebMvcLinkBuilder link1 = linkTo(methodOn(this.getClass()).findById(id));
		WebMvcLinkBuilder link2 = linkTo(methodOn(this.getClass()).findAll());
		resource.add(link1.withRel("attendance-self-info"));
		resource.add(link2.withRel("attendance-all-info"));

		return resource;
	}

	//esto es para manejar la asistencia de una clase en bloque
	@PostMapping("/bulk-class")
	public ResponseEntity<ClassAttendanceResponseDTO> saveClassAttendance(@RequestBody ClassAttendanceDTO classAttendanceDto) 
			throws Exception {
		ClassAttendanceResponseDTO response = service.saveClassAttendance(classAttendanceDto);
		return ResponseEntity.ok(response);
	}

	private AttendanceDTO convertToDto(Attendance obj) {
		return modelMapper.map(obj, AttendanceDTO.class);
	}

	private Attendance convertToEntity(AttendanceDTO dto) {
		return modelMapper.map(dto, Attendance.class);
	}
}
