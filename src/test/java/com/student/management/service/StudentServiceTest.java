package com.student.management.service;

import com.student.management.dto.StudentDashboardStats;
import com.student.management.export.StudentCsvExporter;
import com.student.management.model.EnrollmentStatus;
import com.student.management.model.Student;
import com.student.management.model.StudentFactory;
import com.student.management.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentCsvExporter studentCsvExporter;

    @Mock
    private StudentFactory studentFactory;

    @InjectMocks
    private StudentService studentService;

    @Test
    void addStudentShouldSaveWhenEmailIsUnique() {
        Student input = new Student("  Arjun Sharma  ", "  arjun@example.com  ", "  B.Tech CSE  ");
        Student createdByFactory = new Student("Arjun Sharma", "arjun@example.com", "B.Tech CSE");

        when(studentRepository.existsByEmailIgnoreCase("arjun@example.com")).thenReturn(false);
        when(studentFactory.createNew("  Arjun Sharma  ", "arjun@example.com", "  B.Tech CSE  ", EnrollmentStatus.ACTIVE))
                .thenReturn(createdByFactory);
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> {
            Student saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        Student saved = studentService.addStudent(input);

        assertEquals(1L, saved.getId());
        assertEquals("Arjun Sharma", saved.getName());
        assertEquals("arjun@example.com", saved.getEmail());
        assertEquals("B.Tech CSE", saved.getCourse());
        assertEquals(EnrollmentStatus.ACTIVE, saved.getStatus());
    }

    @Test
    void addStudentShouldThrowWhenEmailAlreadyExists() {
        Student input = new Student("A", "a@example.com", "B.Tech CSE");

        when(studentRepository.existsByEmailIgnoreCase("a@example.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> studentService.addStudent(input));
    }

    @Test
    void updateStudentShouldThrowWhenStudentNotFound() {
        Student updated = new Student("Arjun", "arjun@example.com", "B.Tech CSE");

        when(studentRepository.findById(42L)).thenReturn(Optional.empty());

        assertThrows(StudentNotFoundException.class, () -> studentService.updateStudent(42L, updated));
    }

    @Test
    void updateStudentShouldThrowWhenDuplicateEmailBelongsToAnotherRecord() {
        Student existing = new Student("Arjun", "a@example.com", "B.Tech CSE");
        existing.setId(1L);

        Student updated = new Student("Arjun", "dup@example.com", "B.Tech CSE");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(studentRepository.existsByEmailIgnoreCaseAndIdNot("dup@example.com", 1L)).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> studentService.updateStudent(1L, updated));
    }

    @Test
    void searchStudentsShouldFilterByQueryAndCourse() {
        Student s1 = new Student("Arjun", "arjun@example.com", "B.Tech CSE");
        s1.setId(2L);
        s1.setStatus(EnrollmentStatus.ACTIVE);

        Student s2 = new Student("Nisha", "nisha@example.com", "MBA");
        s2.setId(1L);
        s2.setStatus(EnrollmentStatus.ALUMNI);

        when(studentRepository.findAll()).thenReturn(List.of(s1, s2));

        List<Student> result = studentService.searchStudents("arjun", "B.Tech CSE");

        assertEquals(1, result.size());
        assertEquals("Arjun", result.get(0).getName());
    }

    @Test
    void getDashboardStatsShouldAggregateCounts() {
        Student s1 = new Student("A", "a@example.com", "B.Tech CSE");
        s1.setId(3L);
        s1.setStatus(EnrollmentStatus.ACTIVE);

        Student s2 = new Student("B", "b@example.com", "B.Tech CSE");
        s2.setId(2L);
        s2.setStatus(EnrollmentStatus.INACTIVE);

        Student s3 = new Student("C", "c@example.com", "MBA");
        s3.setId(1L);
        s3.setStatus(EnrollmentStatus.ALUMNI);

        when(studentRepository.findAll()).thenReturn(List.of(s1, s2, s3));

        StudentDashboardStats stats = studentService.getDashboardStats();

        assertEquals(3, stats.getTotalCount());
        assertEquals(1, stats.getActiveCount());
        assertEquals(1, stats.getInactiveCount());
        assertEquals(1, stats.getAlumniCount());
        assertTrue(stats.getStudentsByCourse().containsKey("B.Tech CSE"));
        assertTrue(stats.getStudentsByCourse().containsKey("MBA"));

        verify(studentRepository).findAll();
    }
}
