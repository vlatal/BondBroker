package com.bonds4all.controllers;

import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class RootController {

    RootController() {
    }

    @GetMapping("/")
    Resource root() {

        return new Resource(linkTo(methodOn(ClientController.class).all()).withRel("clients"));
    }
}
