package com.bonds4all.controllers;

import com.bonds4all.exceptions.BondBrokerException;
import com.bonds4all.exceptions.ClientNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class BondBrokerExceptionAdvice {

    @ResponseBody
    @ExceptionHandler(BondBrokerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String BondBrokerExceptionHandler(BondBrokerException ex) {
        return ex.getMessage();
    }
}