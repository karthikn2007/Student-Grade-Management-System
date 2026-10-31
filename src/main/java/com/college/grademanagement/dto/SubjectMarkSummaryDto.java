package com.college.grademanagement.dto;

public class SubjectMarkSummaryDto {

    private Long subjectId;
    private String subjectCode;
    private String subjectName;
    private Double maxMarks;
    private Double passingMarks;
    private Double marksObtained;
    private String grade;
    private String status; // PASS or FAIL
    private String remarks;

    public SubjectMarkSummaryDto() {
    }

    public SubjectMarkSummaryDto(Long subjectId, String subjectCode, String subjectName,
                                 Double maxMarks, Double passingMarks, Double marksObtained,
                                 String grade, String status, String remarks) {
        this.subjectId = subjectId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.maxMarks = maxMarks;
        this.passingMarks = passingMarks;
        this.marksObtained = marksObtained;
        this.grade = grade;
        this.status = status;
        this.remarks = remarks;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Double getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(Double maxMarks) {
        this.maxMarks = maxMarks;
    }

    public Double getPassingMarks() {
        return passingMarks;
    }

    public void setPassingMarks(Double passingMarks) {
        this.passingMarks = passingMarks;
    }

    public Double getMarksObtained() {
        return marksObtained;
    }

    public void setMarksObtained(Double marksObtained) {
        this.marksObtained = marksObtained;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
