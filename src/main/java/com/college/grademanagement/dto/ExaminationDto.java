package com.college.grademanagement.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public class ExaminationDto {

    private Long id;

    @NotBlank(message = "Exam code is required")
    private String examCode;

    @NotBlank(message = "Exam name is required")
    private String examName;

    @NotBlank(message = "Academic year is required")
    private String academicYear;

    @NotBlank(message = "Semester is required")
    private String semester;

    private LocalDate startDate;
    private LocalDate endDate;

    public ExaminationDto() {
    }

    public ExaminationDto(Long id, String examCode, String examName, String academicYear, String semester, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.examCode = examCode;
        this.examName = examName;
        this.academicYear = academicYear;
        this.semester = semester;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
