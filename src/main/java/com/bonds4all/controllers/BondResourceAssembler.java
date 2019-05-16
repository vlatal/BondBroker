package com.bonds4all.controllers;

import com.bonds4all.models.Bond;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
class BondResourceAssembler implements ResourceAssembler<Bond, Resource<Bond>> {

    @Override
    public Resource<Bond> toResource(Bond bond) {

        return new Resource<>(bond,
                ControllerLinkBuilder.linkTo(methodOn(BondController.class).one(bond.getBondId())).withSelfRel(),
                linkTo(methodOn(BondController.class).all()).withRel("bonds"));
    }
}