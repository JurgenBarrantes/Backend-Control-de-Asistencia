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

import com.systems.dto.AssistDTO;
import com.systems.model.Assist;
import com.systems.service.IAssistService;

import lombok.RequiredArgsConstructor;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/assists")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AssistController {
    private final IAssistService service;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<List<AssistDTO>> findAll() throws Exception {
        List<AssistDTO> list = service.findAll().stream().map(this::convertToDto).toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssistDTO> findById(@PathVariable("id") Integer id) throws Exception {
        AssistDTO obj = convertToDto(service.findById(id));
        return ResponseEntity.ok(obj);
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody AssistDTO dto) throws Exception {
        Assist obj = service.save(convertToEntity(dto));
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(obj.getIdAssist()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssistDTO> update(@PathVariable("id") Integer id, @RequestBody AssistDTO dto)
            throws Exception {
        dto.setIdAssist(id);
        Assist obj = service.update(convertToEntity(dto), id);
        AssistDTO dto1 = convertToDto(obj);
        return ResponseEntity.ok(dto1);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) throws Exception {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("hateoas/{id}")
    public EntityModel<AssistDTO> findByIdHateoas(@PathVariable("id") Integer id) throws Exception {
        Assist obj = service.findById(id);
        EntityModel<AssistDTO> resource = EntityModel.of(convertToDto(obj));

        WebMvcLinkBuilder link1 = linkTo(methodOn(this.getClass()).findById(id));
        WebMvcLinkBuilder link2 = linkTo(methodOn(this.getClass()).findAll());
        resource.add(link1.withRel("assist-self-info"));
        resource.add(link2.withRel("assist-all-info"));

        return resource;
    }

    private AssistDTO convertToDto(Assist obj) {
        return modelMapper.map(obj, AssistDTO.class);
    }

    private Assist convertToEntity(AssistDTO dto) {
        return modelMapper.map(dto, Assist.class);
    }
}
