package com.college.grademanagement.service;

import com.college.grademanagement.dto.StudentResultDto;
import com.college.grademanagement.dto.SubjectMarkSummaryDto;
import com.college.grademanagement.exception.ResourceNotFoundException;
import com.college.grademanagement.model.Examination;
import com.college.grademanagement.model.Mark;
import com.college.grademanagement.model.Student;
import com.college.grademanagement.repository.ExaminationRepository;
import com.college.grademanagement.repository.MarkRepository;
import com.college.grademanagement.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResultService {

    private final StudentRepository studentRepository;
    private final ExaminationRepository examinationRepository;
    private final MarkRepository markRepository;

    public ResultService(StudentRepository studentRepository,
                         ExaminationRepository examinationRepository,
                         MarkRepository markRepository) {
        this.studentRepository = studentRepository;
        this.examinationRepository = examinationRepository;
        this.markRepository = markRepository;
    }

    public StudentResultDto getStudentResult(String rollNumber, Long examinationId) {
        Student student = studentRepository.findByRollNumberIgnoreCase(rollNumber.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with roll number: " + rollNumber));

        Examination examination;
        if (examinationId != null) {
            examination = examinationRepository.findById(examinationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Examination not found with ID: " + examinationId));
        } else {
            // Pick latest examination with marks for this student or first exam
            List<Mark> marks = markRepository.findByStudentId(student.getId());
            if (!marks.isEmpty()) {
                examination = marks.get(marks.size() - 1).getExamination();
            } else {
                List<Examination> exams = examinationRepository.findAll();
                if (exams.isEmpty()) {
                    throw new ResourceNotFoundException("No examinations configured in the system");
                }
                examination = exams.get(0);
            }
        }

        List<Mark> studentMarks = markRepository.findByStudentIdAndExaminationId(student.getId(), examination.getId());

        StudentResultDto result = new StudentResultDto();
        result.setStudentId(student.getId());
        result.setRollNumber(student.getRollNumber());
        result.setFullName(student.getFullName());
        result.setEmail(student.getEmail());
        result.setPhoneNumber(student.getPhoneNumber());
        result.setDepartment(student.getDepartment());
        result.setAcademicYear(student.getAcademicYear());
        result.setSemester(student.getSemester());
        result.setDateOfBirth(student.getDateOfBirth());

        result.setExaminationId(examination.getId());
        result.setExamCode(examination.getExamCode());
        result.setExamName(examination.getExamName());
        result.setExamAcademicYear(examination.getAcademicYear());

        List<SubjectMarkSummaryDto> subjectSummaries = new ArrayList<>();
        double totalObtained = 0.0;
        double totalMax = 0.0;
        int passedCount = 0;
        int failedCount = 0;
        boolean allPassed = true;

        for (Mark m : studentMarks) {
            SubjectMarkSummaryDto summary = new SubjectMarkSummaryDto(
                    m.getSubject().getId(),
                    m.getSubject().getSubjectCode(),
                    m.getSubject().getSubjectName(),
                    m.getSubject().getMaxMarks(),
                    m.getSubject().getPassingMarks(),
                    m.getMarksObtained(),
                    m.getGrade(),
                    m.getStatus(),
                    m.getRemarks()
            );
            subjectSummaries.add(summary);

            totalObtained += m.getMarksObtained();
            totalMax += m.getSubject().getMaxMarks();

            if ("PASS".equalsIgnoreCase(m.getStatus())) {
                passedCount++;
            } else {
                failedCount++;
                allPassed = false;
            }
        }

        result.setSubjectMarks(subjectSummaries);
        result.setTotalSubjects(studentMarks.size());
        result.setTotalMarksObtained(Math.round(totalObtained * 100.0) / 100.0);
        result.setTotalMaxMarks(Math.round(totalMax * 100.0) / 100.0);
        result.setPassedSubjectsCount(passedCount);
        result.setFailedSubjectsCount(failedCount);

        double percentage = (totalMax > 0) ? (totalObtained / totalMax) * 100.0 : 0.0;
        result.setPercentage(Math.round(percentage * 100.0) / 100.0);

        // Overall Grade derived from overall percentage
        result.setOverallGrade(MarkService.calculateGrade(totalObtained, totalMax));

        // Academic Rule: Student passes only if they meet the subject's passing marks in EVERY subject
        if (studentMarks.isEmpty()) {
            result.setOverallStatus("PENDING");
        } else if (allPassed && failedCount == 0) {
            result.setOverallStatus("PASS");
        } else {
            result.setOverallStatus("FAIL");
        }

        return result;
    }
}
