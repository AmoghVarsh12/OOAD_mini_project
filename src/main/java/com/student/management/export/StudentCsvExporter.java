package com.student.management.export;

import com.student.management.model.Student;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StudentCsvExporter {

    public String toCsv(List<Student> students) {
        StringBuilder builder = new StringBuilder();
        builder.append("ID,Name,Email,Course,Status\n");

        for (Student student : students) {
            builder.append(student.getId() == null ? "" : student.getId()).append(',')
                    .append(escape(student.getName())).append(',')
                    .append(escape(student.getEmail())).append(',')
                    .append(escape(student.getCourse())).append(',')
                    .append(student.getStatus() == null ? "" : student.getStatus().name())
                    .append('\n');
        }

        return builder.toString();
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }

        String sanitized = value.replace("\"", "\"\"");
        return "\"" + sanitized + "\"";
    }
}
