package com.college.grademanagement.service;

import com.college.grademanagement.dto.ExaminationDto;
import com.college.grademanagement.exception.DuplicateResourceException;
import com.college.grademanagement.exception.ResourceNotFoundException;
import com.college.grademanagement.model.Examination;
import com.college.grademanagement.repository.ExaminationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExaminationService {

    private final ExaminationRepository examinationRepository;

    public ExaminationService(ExaminationRepository examinationRepository) {
        this.examinationRepository = examinationRepository;
    }

    public List<ExaminationDto> getAllExaminations() {
        return examinationRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ExaminationDto getExaminationById(Long id) {
        Examination exam = examinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examination not found with ID: " + id));
        return toDto(exam);
    }

    @Transactional
    public ExaminationDto createExamination(ExaminationDto dto) {
        if (examinationRepository.existsByExamCode(dto.getExamCode().trim())) {
            throw new DuplicateResourceException("Examination code already exists: " + dto.getExamCode());
        }

        Examination exam = new Examination(
                dto.getExamCode().trim().toUpperCase(),
                dto.getExamName().trim(),
                dto.getAcademicYear().trim(),
                dto.getSemester().trim(),
                dto.getStartDate(),
                dto.getEndDate()
        );

        Examination saved = examinationRepository.save(exam);
        return toDto(saved);
    }

    @Transactional
    public ExaminationDto updateExamination(Long id, ExaminationDto dto) {
        Examination exam = examinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examination not found with ID: " + id));

        String newCode = dto.getExamCode().trim().toUpperCase();
        if (!exam.getExamCode().equalsIgnoreCase(newCode) && examinationRepository.existsByExamCode(newCode)) {
            throw new DuplicateResourceException("Examination code already in use: " + newCode);
        }

        exam.setExamCode(newCode);
        exam.setExamName(dto.getExamName().trim());
        exam.setAcademicYear(dto.getAcademicYear().trim());
        exam.setSemester(dto.getSemester().trim());
        exam.setStartDate(dto.getStartDate());
        exam.setEndDate(dto.getEndDate());

        Examination updated = examinationRepository.save(exam);
        return toDto(updated);
    }

    @Transactional
    public void deleteExamination(Long id) {
        if (!examinationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Examination not found with ID: " + id);
        }
        examinationRepository.deleteById(id);
    }

    public ExaminationDto toDto(Examination exam) {
        return new ExaminationDto(
                exam.getId(),
                exam.getExamCode(),
                exam.getExamName(),
                exam.getAcademicYear(),
                exam.getSemester(),
                exam.getStartDate(),
                exam.getEndDate()
        );
    }
}
