package com.student.management.controller;

import com.student.management.dto.StudentDashboardStats;
import com.student.management.dto.StudentResponse;
import com.student.management.dto.StudentResponseAdapter;
import com.student.management.model.Student;
import com.student.management.service.StudentNotFoundException;
import com.student.management.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
public class StudentController {

    private final StudentService studentService;
    private final StudentResponseAdapter studentResponseAdapter;

    public StudentController(StudentService studentService,
                             StudentResponseAdapter studentResponseAdapter) {
        this.studentService = studentService;
        this.studentResponseAdapter = studentResponseAdapter;
    }

    @GetMapping("/")
    public String homePage(@RequestParam(defaultValue = "") String query,
                           @RequestParam(defaultValue = "") String course,
                           Model model) {
        populateModel(model, new Student(), query, course, null);
        return "index";
    }

    @PostMapping("/students")
    public String addStudentForm(@Valid @ModelAttribute("student") Student student,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {

        if (bindingResult.hasErrors()) {
            populateModel(model, student, "", "", null);
            return "index";
        }

        studentService.addStudent(student);
        redirectAttributes.addFlashAttribute("successMessage",
                "Student \"" + student.getName() + "\" registered successfully.");
        return "redirect:/";
    }

    @GetMapping("/api/students")
    @ResponseBody
    public ResponseEntity<List<StudentResponse>> getStudents(
            @RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "") String course) {
        return ResponseEntity.ok(
                studentService.searchStudents(query, course)
                        .stream()
                        .map(studentResponseAdapter::adapt)
                        .toList()
        );
    }

    @GetMapping("/api/students/{id}")
    @ResponseBody
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        Student student = studentService.getStudentById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
        return ResponseEntity.ok(studentResponseAdapter.adapt(student));
    }

    @GetMapping("/api/students/course")
    @ResponseBody
    public ResponseEntity<List<StudentResponse>> getStudentsByCourse(@RequestParam String name) {
        return ResponseEntity.ok(
                studentService.getStudentsByCourse(name)
                        .stream()
                        .map(studentResponseAdapter::adapt)
                        .toList()
        );
    }

    @PostMapping("/api/students")
    @ResponseBody
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody Student student) {
        Student created = studentService.addStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(studentResponseAdapter.adapt(created));
    }

    @PutMapping("/api/students/{id}")
    @ResponseBody
    public ResponseEntity<StudentResponse> updateStudent(@PathVariable Long id,
                                                         @Valid @RequestBody Student student) {
        return ResponseEntity.ok(studentResponseAdapter.adapt(studentService.updateStudent(id, student)));
    }

    @DeleteMapping("/api/students/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/students/stats")
    @ResponseBody
    public ResponseEntity<StudentDashboardStats> getStats() {
        return ResponseEntity.ok(studentService.getDashboardStats());
    }

    @GetMapping(value = "/api/students/export", produces = "text/csv")
    @ResponseBody
    public ResponseEntity<byte[]> exportStudentsAsCsv() {
        byte[] csvBytes = studentService.exportStudentsAsCsv().getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students.csv")
                .contentType(new MediaType("text", "csv"))
                .body(csvBytes);
    }

    private void populateModel(Model model,
                               Student studentForm,
                               String query,
                               String course,
                               String inlineErrorMessage) {
        List<Student> filteredStudents = studentService.searchStudents(query, course);
        StudentDashboardStats stats = studentService.getDashboardStats();

        model.addAttribute("student", studentForm);
        model.addAttribute("students", filteredStudents);
        model.addAttribute("totalCount", studentService.getTotalCount());
        model.addAttribute("filteredCount", filteredStudents.size());
        model.addAttribute("stats", stats);
        model.addAttribute("courses", studentService.getAvailableCourses());
        model.addAttribute("searchQuery", query == null ? "" : query);
        model.addAttribute("selectedCourse", course == null ? "" : course);

        if (inlineErrorMessage != null && !inlineErrorMessage.isBlank()) {
            model.addAttribute("errorMessage", inlineErrorMessage);
        }
    }
}
