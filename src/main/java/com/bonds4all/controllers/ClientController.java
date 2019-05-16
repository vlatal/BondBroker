package com.bonds4all.controllers;

import com.bonds4all.exceptions.ClientNotFoundException;
import com.bonds4all.models.Bond;
import com.bonds4all.models.Client;
import com.bonds4all.models.Record;
import com.bonds4all.repositories.ClientRepository;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientRepository repository;
    private final ClientResourceAssembler assembler;

    ClientController(ClientRepository repository, ClientResourceAssembler assembler) {

        this.repository = repository;
        this.assembler = assembler;
    }

    // Aggregate root
    @GetMapping
    Resources<Resource<Client>> all() {
        List<Resource<Client>> clients = repository.findAll().stream()
                .map(assembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(clients,
                linkTo(methodOn(ClientController.class).all()).withSelfRel());
    }

    @PostMapping
    Resource<Client> newClient(@RequestBody Client newClient) {
        Client client = repository.save(newClient);
        return assembler.toResource(client);
    }

    // Single item
    @GetMapping("/{id}")
    Resource<Client> one(@PathVariable Long id) {

        Client client = repository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        return assembler.toResource(client);
    }

    @PutMapping("/{id}")
    Client replaceClient(@RequestBody Client newClient, @PathVariable Long id) {

        return repository.findById(id)
                .map(Client -> {
                    Client.setFamilyName(newClient.getFamilyName());
                    Client.setGivenName(newClient.getGivenName());
                    Client.setOtherPersonalData(newClient.getOtherPersonalData());
                    return repository.save(Client);
                })
                .orElseGet(() -> {
                    newClient.setClientId(id);
                    return repository.save(newClient);
                });
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteClient(@PathVariable Long id) {
        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
/*
    @GetMapping("/{clientId}/records")
    Resources<Resource<Record>> records(@PathVariable Long clientId) {
        Client client = repository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));

        List<Resource<Record>> records = client.getRecords().stream()
                .map(recordAssembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(records,
                linkTo(methodOn(RecordController.class).all()).withSelfRel());
    }

    @GetMapping("/{clientId}/bonds")
    Resources<Resource<Bond>> bonds(@PathVariable Long clientId) {
        Client client = repository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));

        List<Resource<Bond>> bonds = client.getBonds().stream()
                .map(bondAssembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(bonds,
                linkTo(methodOn(RecordController.class).all()).withSelfRel());
    }*/
}
