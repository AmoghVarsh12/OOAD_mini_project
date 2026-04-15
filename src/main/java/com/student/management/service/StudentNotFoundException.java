package com.student.management.service;

public class StudentNotFoundException extends RuntimeException {

    public StudentNotFoundException(Long id) {
        super("Student with ID " + id + " not found.");
    }
}
