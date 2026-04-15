package com.student.management.dto;

import com.student.management.model.Student;
import org.springframework.stereotype.Component;

@Component
public class StudentResponseAdapter {

    public StudentResponse adapt(Student student) {
        StudentResponse response = new StudentResponse();
        response.setId(student.getId());
        response.setName(student.getName());
        response.setEmail(student.getEmail());
        response.setCourse(student.getCourse());
        response.setStatus(student.getStatus());
        response.setCreatedAt(student.getCreatedAt());
        response.setUpdatedAt(student.getUpdatedAt());
        return response;
    }
}
