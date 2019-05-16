package com.bonds4all.controllers;

import com.bonds4all.models.Record;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
class RecordResourceAssembler implements ResourceAssembler<Record, Resource<Record>> {

    @Override
    public Resource<Record> toResource(Record record) {

        return new Resource<>(record,
                ControllerLinkBuilder.linkTo(methodOn(RecordController.class).one(record.getRecordId())).withSelfRel(),
                linkTo(methodOn(RecordController.class).all()).withRel("records"));
    }
}