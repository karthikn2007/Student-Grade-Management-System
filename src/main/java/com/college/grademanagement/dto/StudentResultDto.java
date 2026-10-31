package com.college.grademanagement.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudentResultDto {

    // Student Information
    private Long studentId;
    private String rollNumber;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String department;
    private String academicYear;
    private String semester;
    private LocalDate dateOfBirth;

    // Examination Information
    private Long examinationId;
    private String examCode;
    private String examName;
    private String examAcademicYear;

    // Subject Breakdown
    private List<SubjectMarkSummaryDto> subjectMarks = new ArrayList<>();

    // Aggregates & Result Evaluation
    private Double totalMarksObtained = 0.0;
    private Double totalMaxMarks = 0.0;
    private Double percentage = 0.0;
    private String overallGrade; // A+, A, B, C, D, E, F
    private String overallStatus; // PASS or FAIL
    private int totalSubjects = 0;
    private int passedSubjectsCount = 0;
    private int failedSubjectsCount = 0;

    public StudentResultDto() {
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Long getExaminationId() {
        return examinationId;
    }

    public void setExaminationId(Long examinationId) {
        this.examinationId = examinationId;
    }

    public String getExamCode() {
        return examCode;
    }

    public void setExamCode(String examCode) {
        this.examCode = examCode;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getExamAcademicYear() {
        return examAcademicYear;
    }

    public void setExamAcademicYear(String examAcademicYear) {
        this.examAcademicYear = examAcademicYear;
    }

    public List<SubjectMarkSummaryDto> getSubjectMarks() {
        return subjectMarks;
    }

    public void setSubjectMarks(List<SubjectMarkSummaryDto> subjectMarks) {
        this.subjectMarks = subjectMarks;
    }

    public Double getTotalMarksObtained() {
        return totalMarksObtained;
    }

    public void setTotalMarksObtained(Double totalMarksObtained) {
        this.totalMarksObtained = totalMarksObtained;
    }

    public Double getTotalMaxMarks() {
        return totalMaxMarks;
    }

    public void setTotalMaxMarks(Double totalMaxMarks) {
        this.totalMaxMarks = totalMaxMarks;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public String getOverallGrade() {
        return overallGrade;
    }

    public void setOverallGrade(String overallGrade) {
        this.overallGrade = overallGrade;
    }

    public String getOverallStatus() {
        return overallStatus;
    }

    public void setOverallStatus(String overallStatus) {
        this.overallStatus = overallStatus;
    }

    public int getTotalSubjects() {
        return totalSubjects;
    }

    public void setTotalSubjects(int totalSubjects) {
        this.totalSubjects = totalSubjects;
    }

    public int getPassedSubjectsCount() {
        return passedSubjectsCount;
    }

    public void setPassedSubjectsCount(int passedSubjectsCount) {
        this.passedSubjectsCount = passedSubjectsCount;
    }

    public int getFailedSubjectsCount() {
        return failedSubjectsCount;
    }

    public void setFailedSubjectsCount(int failedSubjectsCount) {
        this.failedSubjectsCount = failedSubjectsCount;
    }
}
