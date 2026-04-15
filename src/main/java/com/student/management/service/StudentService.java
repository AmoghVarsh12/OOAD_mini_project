package com.student.management.service;

import com.student.management.dto.StudentDashboardStats;
import com.student.management.export.StudentCsvExporter;
import com.student.management.model.EnrollmentStatus;
import com.student.management.model.Student;
import com.student.management.model.StudentFactory;
import com.student.management.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentCsvExporter studentCsvExporter;
    private final StudentFactory studentFactory;

    public StudentService(StudentRepository studentRepository,
                          StudentCsvExporter studentCsvExporter,
                          StudentFactory studentFactory) {
        this.studentRepository = studentRepository;
        this.studentCsvExporter = studentCsvExporter;
        this.studentFactory = studentFactory;
    }

    public Student addStudent(Student student) {
        String email = normalize(student.getEmail());
        if (studentRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateEmailException(email);
        }

        Student newStudent = studentFactory.createNew(
                student.getName(),
                email,
                student.getCourse(),
                student.getStatus() == null ? EnrollmentStatus.ACTIVE : student.getStatus()
        );

        return studentRepository.save(newStudent);
    }

    public Student updateStudent(Long id, Student updatedStudent) {
        Student current = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));

        String email = normalize(updatedStudent.getEmail());
        if (studentRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new DuplicateEmailException(email);
        }

        studentFactory.applyUpdates(
                current,
                updatedStudent.getName(),
                email,
                updatedStudent.getCourse(),
                updatedStudent.getStatus()
        );

        return studentRepository.save(current);
    }

    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException(id);
        }
        studentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return studentRepository.findAll().stream()
                .sorted(Comparator.comparing(Student::getId).reversed())
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Student> getStudentsByCourse(String course) {
        if (isBlank(course)) {
            return getAllStudents();
        }

        return studentRepository.findByCourseIgnoreCase(course).stream()
                .sorted(Comparator.comparing(Student::getId).reversed())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Student> searchStudents(String query, String course) {
        String normalizedQuery = normalize(query).toLowerCase();
        String normalizedCourse = normalize(course).toLowerCase();

        return getAllStudents().stream()
                .filter(student -> {
                    if (!normalizedCourse.isEmpty() && !normalize(student.getCourse()).equalsIgnoreCase(normalizedCourse)) {
                        return false;
                    }

                    if (normalizedQuery.isEmpty()) {
                        return true;
                    }

                    return contains(student.getName(), normalizedQuery)
                            || contains(student.getEmail(), normalizedQuery)
                            || contains(student.getCourse(), normalizedQuery)
                            || (student.getStatus() != null
                            && student.getStatus().name().toLowerCase().contains(normalizedQuery));
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public long getTotalCount() {
        return studentRepository.count();
    }

    @Transactional(readOnly = true)
    public StudentDashboardStats getDashboardStats() {
        List<Student> students = getAllStudents();

        long activeCount = students.stream()
                .filter(student -> student.getStatus() == EnrollmentStatus.ACTIVE)
                .count();

        long inactiveCount = students.stream()
                .filter(student -> student.getStatus() == EnrollmentStatus.INACTIVE)
                .count();

        long alumniCount = students.stream()
                .filter(student -> student.getStatus() == EnrollmentStatus.ALUMNI)
                .count();

        Map<String, Long> byCourse = students.stream()
                .collect(Collectors.groupingBy(
                        student -> normalize(student.getCourse()),
                        Collectors.counting()
                ));

        Map<String, Long> sortedByCourse = byCourse.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (left, right) -> left,
                        LinkedHashMap::new
                ));

        return new StudentDashboardStats(
                students.size(),
                activeCount,
                inactiveCount,
                alumniCount,
                sortedByCourse
        );
    }

    @Transactional(readOnly = true)
    public String exportStudentsAsCsv() {
        return studentCsvExporter.toCsv(getAllStudents());
    }

    @Transactional(readOnly = true)
    public List<String> getAvailableCourses() {
        return getAllStudents().stream()
                .map(Student::getCourse)
                .map(this::normalize)
                .filter(course -> !course.isEmpty())
                .distinct()
                .sorted()
                .toList();
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean contains(String source, String query) {
        return normalize(source).toLowerCase().contains(query);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
