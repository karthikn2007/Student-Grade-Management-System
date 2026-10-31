package com.college.grademanagement.service;

import com.college.grademanagement.dto.MarkDto;
import com.college.grademanagement.dto.MarkEntryRequest;
import com.college.grademanagement.exception.BadRequestException;
import com.college.grademanagement.exception.ResourceNotFoundException;
import com.college.grademanagement.model.Examination;
import com.college.grademanagement.model.Mark;
import com.college.grademanagement.model.Student;
import com.college.grademanagement.model.Subject;
import com.college.grademanagement.repository.ExaminationRepository;
import com.college.grademanagement.repository.MarkRepository;
import com.college.grademanagement.repository.StudentRepository;
import com.college.grademanagement.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MarkService {

    private final MarkRepository markRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final ExaminationRepository examinationRepository;

    public MarkService(MarkRepository markRepository,
                       StudentRepository studentRepository,
                       SubjectRepository subjectRepository,
                       ExaminationRepository examinationRepository) {
        this.markRepository = markRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.examinationRepository = examinationRepository;
    }

    public List<MarkDto> getAllMarks() {
        return markRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public MarkDto getMarkById(Long id) {
        Mark mark = markRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mark record not found with ID: " + id));
        return toDto(mark);
    }

    public List<MarkDto> getMarksByStudentAndExamination(Long studentId, Long examinationId) {
        return markRepository.findByStudentIdAndExaminationId(studentId, examinationId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<MarkDto> getMarksByExamination(Long examinationId) {
        return markRepository.findByExaminationId(examinationId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<MarkDto> getMarksBySubjectAndExamination(Long subjectId, Long examinationId) {
        return markRepository.findBySubjectIdAndExaminationId(subjectId, examinationId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public MarkDto enterOrUpdateMark(MarkEntryRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + request.getStudentId()));

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + request.getSubjectId()));

        Examination examination = examinationRepository.findById(request.getExaminationId())
                .orElseThrow(() -> new ResourceNotFoundException("Examination not found with ID: " + request.getExaminationId()));

        Double marks = request.getMarksObtained();
        if (marks == null) {
            throw new BadRequestException("Marks obtained cannot be null");
        }

        // Strict boundary validation: Cannot fall below 0 or exceed subject maximum marks
        if (marks < 0.0) {
            throw new BadRequestException("Marks obtained cannot be negative (provided: " + marks + ")");
        }
        if (marks > subject.getMaxMarks()) {
            throw new BadRequestException("Marks obtained (" + marks + ") cannot exceed subject maximum marks (" + subject.getMaxMarks() + ")");
        }

        // Automatic Grade and Status Calculation
        String grade = calculateGrade(marks, subject.getMaxMarks());
        String status = determineStatus(marks, subject.getPassingMarks());

        // Check if a mark entry already exists for this student, subject, and exam
        Optional<Mark> existingMarkOpt;
        if (request.getId() != null) {
            existingMarkOpt = markRepository.findById(request.getId());
        } else {
            existingMarkOpt = markRepository.findByStudentIdAndSubjectIdAndExaminationId(
                    student.getId(), subject.getId(), examination.getId());
        }

        Mark mark;
        if (existingMarkOpt.isPresent()) {
            mark = existingMarkOpt.get();
            mark.setMarksObtained(marks);
            mark.setGrade(grade);
            mark.setStatus(status);
            mark.setRemarks(request.getRemarks());
        } else {
            mark = new Mark(student, subject, examination, marks, grade, status, request.getRemarks());
        }

        Mark saved = markRepository.save(mark);
        return toDto(saved);
    }

    @Transactional
    public void deleteMark(Long id) {
        if (!markRepository.existsById(id)) {
            throw new ResourceNotFoundException("Mark record not found with ID: " + id);
        }
        markRepository.deleteById(id);
    }

    /**
     * Calculates the letter grade based on marks obtained and maximum marks.
     * Scale:
     * 90–100: A+
     * 80–89: A
     * 70–79: B
     * 60–69: C
     * 50–59: D
     * 40–49: E
     * Below 40: F
     */
    public static String calculateGrade(double marksObtained, double maxMarks) {
        if (maxMarks <= 0) {
            return "F";
        }
        double percentage = (marksObtained / maxMarks) * 100.0;

        if (percentage >= 90.0) {
            return "A+";
        } else if (percentage >= 80.0) {
            return "A";
        } else if (percentage >= 70.0) {
            return "B";
        } else if (percentage >= 60.0) {
            return "C";
        } else if (percentage >= 50.0) {
            return "D";
        } else if (percentage >= 40.0) {
            return "E";
        } else {
            return "F";
        }
    }

    /**
     * Determines whether the student passed or failed the subject based on subject passing marks.
     */
    public static String determineStatus(double marksObtained, double passingMarks) {
        return (marksObtained >= passingMarks) ? "PASS" : "FAIL";
    }

    public MarkDto toDto(Mark mark) {
        return new MarkDto(
                mark.getId(),
                mark.getStudent().getId(),
                mark.getStudent().getRollNumber(),
                mark.getStudent().getFullName(),
                mark.getSubject().getId(),
                mark.getSubject().getSubjectCode(),
                mark.getSubject().getSubjectName(),
                mark.getExamination().getId(),
                mark.getExamination().getExamName(),
                mark.getMarksObtained(),
                mark.getSubject().getMaxMarks(),
                mark.getSubject().getPassingMarks(),
                mark.getGrade(),
                mark.getStatus(),
                mark.getRemarks()
        );
    }
}
