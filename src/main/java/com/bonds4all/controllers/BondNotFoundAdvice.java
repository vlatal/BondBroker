package com.bonds4all.controllers;

import com.bonds4all.exceptions.BondNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class BondNotFoundAdvice {

    @ResponseBody
    @ExceptionHandler(BondNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String BondNotFoundHandler(BondNotFoundException ex) {
        return ex.getMessage();
    }
}