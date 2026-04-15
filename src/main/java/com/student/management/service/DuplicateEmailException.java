package com.student.management.service;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("A student with email '" + email + "' is already registered.");
    }
}
