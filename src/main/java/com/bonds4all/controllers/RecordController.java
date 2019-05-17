package com.bonds4all.controllers;

import com.bonds4all.exceptions.RecordNotFoundException;
import com.bonds4all.models.Record;
import com.bonds4all.repositories.RecordRepository;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class RecordController {
    private final RecordRepository repository;
    private final RecordResourceAssembler assembler;

    RecordController(RecordRepository repository, RecordResourceAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    // Aggregate root
    @GetMapping("/records")
    Resources<Resource<Record>> all() {
        List<Resource<Record>> records = repository.findAll().stream()
                .map(assembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(records,
                linkTo(methodOn(RecordController.class).all()).withSelfRel());
    }

    @PostMapping("/records")
    Resource<Record> newRecord(@RequestBody Record newRecord) {
        Record record = repository.save(newRecord);
        return assembler.toResource(record);
    }

    @GetMapping("/clients/{clientId}/records")
    Resources<Resource<Record>> clientRecords(@PathVariable long clientId) {
        List<Resource<Record>> records = repository.findByClient_ClientId(clientId).stream()
                .map(assembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(records,
                linkTo(methodOn(RecordController.class).all()).withSelfRel());
    }

    // Single item
    @GetMapping("/records/{id}")
    Resource<Record> one(@PathVariable long id) {

        Record record = repository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(id));

        return assembler.toResource(record);
    }
}
