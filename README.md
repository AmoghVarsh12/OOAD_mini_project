# Student Management System

Spring Boot MVC mini project for UE23CS352B OOAD.

## What is Implemented

This project now includes an end-to-end web application with persistence, validation, analytics, export support, and complete OOAD documentation artifacts.

### Major Use Cases

1. Register student records.
2. View all students.
3. Update student records.
4. Delete student records.

### Minor Use Cases

1. Search students by keyword.
2. Filter students by course.
3. Export students as CSV.
4. View dashboard statistics by status and course.

## Architecture

- MVC Pattern
- Repository Pattern
- Service Layer Pattern
- Factory Pattern (StudentFactory)
- Adapter Pattern (StudentResponseAdapter)

## Tech Stack

- Java 17
- Spring Boot 3.2.0
- Spring MVC + Thymeleaf
- Spring Data JPA + Hibernate
- H2 Database
- Maven
- JUnit 5 + Mockito

## Project Structure

- src/main/java/com/student/management/StudentManagementApplication.java
- src/main/java/com/student/management/controller
- src/main/java/com/student/management/model
- src/main/java/com/student/management/repository
- src/main/java/com/student/management/service
- src/main/java/com/student/management/dto
- src/main/java/com/student/management/export
- src/main/resources/application.properties
- src/main/resources/templates/index.html
- src/test/java/com/student/management/service/StudentServiceTest.java
- docs/PROJECT_REPORT.md
- docs/uml

## Endpoints

### MVC

- GET /
- POST /students

### REST API

- GET /api/students
- GET /api/students/{id}
- GET /api/students/course?name=
- POST /api/students
- PUT /api/students/{id}
- DELETE /api/students/{id}
- GET /api/students/stats
- GET /api/students/export

## Run the Project

1. Ensure Java 17+ and Maven are installed.
2. Run:

   mvn spring-boot:run

3. Open:
- http://localhost:8080
- http://localhost:8080/h2-console

H2 JDBC URL:

jdbc:h2:mem:studentdb

## OOAD Submission Artifacts

- Project report: docs/PROJECT_REPORT.md
- Use case diagram: docs/uml/use_case_diagram.mmd
- Class diagram: docs/uml/class_diagram.mmd
- Activity diagrams: docs/uml/activity_register_student.mmd, docs/uml/activity_update_student.mmd, docs/uml/activity_delete_student.mmd, docs/uml/activity_search_export.mmd
- State diagrams: docs/uml/state_student_lifecycle.mmd, docs/uml/state_registration_request.mmd, docs/uml/state_update_request.mmd, docs/uml/state_export_job.mmd

## Notes

- Fill team member details and contribution ownership in docs/PROJECT_REPORT.md before final PDF submission.
- Add screenshots and public GitHub repository URL in the report.
