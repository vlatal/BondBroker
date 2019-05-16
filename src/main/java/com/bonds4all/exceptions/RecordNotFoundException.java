package com.bonds4all.exceptions;

public class RecordNotFoundException extends RuntimeException {

    public RecordNotFoundException(Long id) {
        super("Could not find Record " + id);
    }
}