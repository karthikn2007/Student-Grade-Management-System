package com.college.grademanagement.dto;

import java.util.HashMap;
import java.util.Map;

public class DashboardStatsDto {

    private long totalStudents;
    private long totalTeachers;
    private long totalSubjects;
    private long totalExaminations;
    private double averageClassPercentage;
    private long passedStudentsCount;
    private long failedStudentsCount;

    // Charts data
    private Map<String, Long> gradeDistribution = new HashMap<>(); // "A+", "A", "B", "C", "D", "E", "F"
    private Map<String, Double> departmentPerformance = new HashMap<>(); // Dept Name -> Avg %

    public DashboardStatsDto() {
    }

    public DashboardStatsDto(long totalStudents, long totalTeachers, long totalSubjects, long totalExaminations,
                             double averageClassPercentage, long passedStudentsCount, long failedStudentsCount) {
        this.totalStudents = totalStudents;
        this.totalTeachers = totalTeachers;
        this.totalSubjects = totalSubjects;
        this.totalExaminations = totalExaminations;
        this.averageClassPercentage = averageClassPercentage;
        this.passedStudentsCount = passedStudentsCount;
        this.failedStudentsCount = failedStudentsCount;
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getTotalTeachers() {
        return totalTeachers;
    }

    public void setTotalTeachers(long totalTeachers) {
        this.totalTeachers = totalTeachers;
    }

    public long getTotalSubjects() {
        return totalSubjects;
    }

    public void setTotalSubjects(long totalSubjects) {
        this.totalSubjects = totalSubjects;
    }

    public long getTotalExaminations() {
        return totalExaminations;
    }

    public void setTotalExaminations(long totalExaminations) {
        this.totalExaminations = totalExaminations;
    }

    public double getAverageClassPercentage() {
        return averageClassPercentage;
    }

    public void setAverageClassPercentage(double averageClassPercentage) {
        this.averageClassPercentage = averageClassPercentage;
    }

    public long getPassedStudentsCount() {
        return passedStudentsCount;
    }

    public void setPassedStudentsCount(long passedStudentsCount) {
        this.passedStudentsCount = passedStudentsCount;
    }

    public long getFailedStudentsCount() {
        return failedStudentsCount;
    }

    public void setFailedStudentsCount(long failedStudentsCount) {
        this.failedStudentsCount = failedStudentsCount;
    }

    public Map<String, Long> getGradeDistribution() {
        return gradeDistribution;
    }

    public void setGradeDistribution(Map<String, Long> gradeDistribution) {
        this.gradeDistribution = gradeDistribution;
    }

    public Map<String, Double> getDepartmentPerformance() {
        return departmentPerformance;
    }

    public void setDepartmentPerformance(Map<String, Double> departmentPerformance) {
        this.departmentPerformance = departmentPerformance;
    }
}
