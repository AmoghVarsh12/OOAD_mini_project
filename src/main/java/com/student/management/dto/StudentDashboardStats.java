package com.student.management.dto;

import java.util.Map;

public class StudentDashboardStats {

    private final long totalCount;
    private final long activeCount;
    private final long inactiveCount;
    private final long alumniCount;
    private final Map<String, Long> studentsByCourse;

    public StudentDashboardStats(long totalCount,
                                 long activeCount,
                                 long inactiveCount,
                                 long alumniCount,
                                 Map<String, Long> studentsByCourse) {
        this.totalCount = totalCount;
        this.activeCount = activeCount;
        this.inactiveCount = inactiveCount;
        this.alumniCount = alumniCount;
        this.studentsByCourse = studentsByCourse;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public long getActiveCount() {
        return activeCount;
    }

    public long getInactiveCount() {
        return inactiveCount;
    }

    public long getAlumniCount() {
        return alumniCount;
    }

    public Map<String, Long> getStudentsByCourse() {
        return studentsByCourse;
    }
}
