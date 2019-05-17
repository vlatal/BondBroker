package com.bonds4all.controllers;

import com.bonds4all.exceptions.ClientNotFoundException;
import com.bonds4all.models.Message;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class ClientNotFoundAdvice {

    @ResponseBody
    @ExceptionHandler(ClientNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Resource<Message> ClientNotFoundHandler(ClientNotFoundException ex) {
        return new Resource<>(Message.createWithNow(ex.getMessage()));
    }
}