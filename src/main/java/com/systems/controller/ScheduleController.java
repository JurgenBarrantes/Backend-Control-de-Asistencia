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

import com.systems.dto.ScheduleDTO;
import com.systems.model.Schedule;
import com.systems.service.IScheduleService;

import lombok.RequiredArgsConstructor;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ScheduleController { //es para manejar las solicitudes relacionadas con los horarios
    private final IScheduleService service;
	private final ModelMapper modelMapper;

	//@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
	@GetMapping
	public ResponseEntity<List<ScheduleDTO>> findAll() throws Exception {

		List<ScheduleDTO> list = service.findAll().stream().map(this::convertToDto).toList();

		return ResponseEntity.ok(list);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ScheduleDTO> findById(@PathVariable("id") Integer id) throws Exception {
		// Publisher obj = service.findById(id);
		// PublisherDTO obj = modelMapper.map(service.findById(id), PublisherDTO.class);
		ScheduleDTO obj = convertToDto(service.findById(id));
		return ResponseEntity.ok(obj);
	}

	@PostMapping
	public ResponseEntity<Void> save(@RequestBody ScheduleDTO dto) throws Exception {
		Schedule obj = service.save(convertToEntity(dto));
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(obj.getIdSchedule()).toUri();
		return ResponseEntity.created(location).build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<ScheduleDTO> update(@PathVariable("id") Integer id, @RequestBody ScheduleDTO dto)
			throws Exception {
		// Publisher obj = service.update(modelMapper.map(dto,Publisher.class), id);
		// PublisherDTO dto1 = modelMapper.map(obj, PublisherDTO.class);
		// return ResponseEntity.ok(dto1);
		dto.setIdSchedule(id);
		Schedule obj = service.update(convertToEntity(dto), id);
		ScheduleDTO dto1 = convertToDto(obj);
		return ResponseEntity.ok(dto1);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Integer id)
			throws Exception {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("hateoas/{id}")
	public EntityModel<ScheduleDTO> findByIdHateoas(@PathVariable("id") Integer id) throws Exception {
		Schedule obj = service.findById(id);
		EntityModel<ScheduleDTO> resource = EntityModel.of(convertToDto(obj));


		WebMvcLinkBuilder link1 = linkTo(methodOn(this.getClass()).findById(id));
		WebMvcLinkBuilder link2 = linkTo(methodOn(this.getClass()).findAll());
		resource.add(link1.withRel("schedule-self-info"));
		resource.add(link2.withRel("schedule-all-info"));

		return resource;
	}

	private ScheduleDTO convertToDto(Schedule obj) {
		return modelMapper.map(obj, ScheduleDTO.class);
	}

	private Schedule convertToEntity(ScheduleDTO dto) {
		return modelMapper.map(dto, Schedule.class);
	}
}
