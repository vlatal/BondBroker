package com.bonds4all.advices;

import com.bonds4all.exceptions.RecordNotFoundException;
import com.bonds4all.models.Message;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class RecordNotFoundAdvice {

    @ResponseBody
    @ExceptionHandler(RecordNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Resource<Message> RecordNotFoundHandler(RecordNotFoundException ex) {
        return new Resource<>(Message.createWithNow(ex.getMessage()));
    }
}