package com.college.grademanagement.service;

import com.college.grademanagement.dto.SubjectDto;
import com.college.grademanagement.exception.BadRequestException;
import com.college.grademanagement.exception.DuplicateResourceException;
import com.college.grademanagement.exception.ResourceNotFoundException;
import com.college.grademanagement.model.Subject;
import com.college.grademanagement.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public List<SubjectDto> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public SubjectDto getSubjectById(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + id));
        return toDto(subject);
    }

    public List<SubjectDto> getSubjectsByDepartment(String department) {
        return subjectRepository.findByDepartment(department).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<SubjectDto> getSubjectsBySemester(String semester) {
        return subjectRepository.findBySemester(semester).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public SubjectDto createSubject(SubjectDto dto) {
        if (subjectRepository.existsBySubjectCode(dto.getSubjectCode().trim())) {
            throw new DuplicateResourceException("Subject code already exists: " + dto.getSubjectCode());
        }

        if (dto.getPassingMarks() > dto.getMaxMarks()) {
            throw new BadRequestException("Passing marks cannot exceed maximum marks");
        }

        Subject subject = new Subject(
                dto.getSubjectCode().trim().toUpperCase(),
                dto.getSubjectName().trim(),
                dto.getDepartment().trim(),
                dto.getSemester().trim(),
                dto.getMaxMarks(),
                dto.getPassingMarks()
        );

        Subject saved = subjectRepository.save(subject);
        return toDto(saved);
    }

    @Transactional
    public SubjectDto updateSubject(Long id, SubjectDto dto) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + id));

        String newCode = dto.getSubjectCode().trim().toUpperCase();
        if (!subject.getSubjectCode().equalsIgnoreCase(newCode) && subjectRepository.existsBySubjectCode(newCode)) {
            throw new DuplicateResourceException("Subject code already in use: " + newCode);
        }

        if (dto.getPassingMarks() > dto.getMaxMarks()) {
            throw new BadRequestException("Passing marks cannot exceed maximum marks");
        }

        subject.setSubjectCode(newCode);
        subject.setSubjectName(dto.getSubjectName().trim());
        subject.setDepartment(dto.getDepartment().trim());
        subject.setSemester(dto.getSemester().trim());
        subject.setMaxMarks(dto.getMaxMarks());
        subject.setPassingMarks(dto.getPassingMarks());

        Subject updated = subjectRepository.save(subject);
        return toDto(updated);
    }

    @Transactional
    public void deleteSubject(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subject not found with ID: " + id);
        }
        subjectRepository.deleteById(id);
    }

    public SubjectDto toDto(Subject subject) {
        return new SubjectDto(
                subject.getId(),
                subject.getSubjectCode(),
                subject.getSubjectName(),
                subject.getDepartment(),
                subject.getSemester(),
                subject.getMaxMarks(),
                subject.getPassingMarks()
        );
    }
}
