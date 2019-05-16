package com.bonds4all.exceptions;

public class BondNotFoundException extends RuntimeException {

    public BondNotFoundException(Long id) {
        super("Could not find Bond " + id);
    }
}