package com.bonds4all.controllers;

import com.bonds4all.models.Client;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
class ClientResourceAssembler implements ResourceAssembler<Client, Resource<Client>> {

    @Override
    public Resource<Client> toResource(Client client) {
        return new Resource<>(client,
                ControllerLinkBuilder.linkTo(methodOn(ClientController.class).one(client.getClientId())).withSelfRel(),
                linkTo(methodOn(ClientController.class).all()).withRel("clients"));
    }
}