package com.student.management.model;

import org.springframework.stereotype.Component;

@Component
public class StudentFactory {

    public Student createNew(String name, String email, String course, EnrollmentStatus status) {
        Student student = new Student();
        student.setName(normalize(name));
        student.setEmail(normalize(email));
        student.setCourse(normalize(course));
        student.setStatus(status == null ? EnrollmentStatus.ACTIVE : status);
        return student;
    }

    public void applyUpdates(Student target,
                             String name,
                             String email,
                             String course,
                             EnrollmentStatus status) {
        target.setName(normalize(name));
        target.setEmail(normalize(email));
        target.setCourse(normalize(course));
        target.setStatus(status == null ? EnrollmentStatus.ACTIVE : status);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
