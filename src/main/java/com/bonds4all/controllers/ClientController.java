package com.bonds4all.controllers;

import com.bonds4all.exceptions.ClientNotFoundException;
import com.bonds4all.models.Bond;
import com.bonds4all.models.Client;
import com.bonds4all.models.Record;
import com.bonds4all.repositories.ClientRepository;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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
    Resource<Client> one(@PathVariable long id) {

        Client client = repository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        return assembler.toResource(client);
    }

    @PutMapping("/{id}")
    Resource<Client> replaceClient(@RequestBody Client newClient, @PathVariable long id) {

        Client savedClient = repository.findById(id)
                .map(client -> {
                    client.setFamilyName(newClient.getFamilyName());
                    client.setGivenName(newClient.getGivenName());
                    client.setOtherPersonalData(newClient.getOtherPersonalData());
                    return repository.save(client);
                })
                .orElseGet(() -> {
                    newClient.setClientId(id);
                    return repository.save(newClient);
                });

        return assembler.toResource(savedClient);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    Resource<Client> deleteClient(@PathVariable long id) {

        Client client = repository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        repository.deleteById(id);

        // Say bye to deleted item
        return assembler.toResource(client);
    }
}
