package com.college.grademanagement.controller;

import com.college.grademanagement.dto.ExaminationDto;
import com.college.grademanagement.service.ExaminationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/examinations")
public class ExaminationController {

    private final ExaminationService examinationService;

    public ExaminationController(ExaminationService examinationService) {
        this.examinationService = examinationService;
    }

    @GetMapping
    public ResponseEntity<List<ExaminationDto>> getAllExaminations() {
        return ResponseEntity.ok(examinationService.getAllExaminations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExaminationDto> getExaminationById(@PathVariable Long id) {
        return ResponseEntity.ok(examinationService.getExaminationById(id));
    }

    @PostMapping
    public ResponseEntity<ExaminationDto> createExamination(@Valid @RequestBody ExaminationDto dto) {
        ExaminationDto created = examinationService.createExamination(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExaminationDto> updateExamination(@PathVariable Long id, @Valid @RequestBody ExaminationDto dto) {
        ExaminationDto updated = examinationService.updateExamination(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteExamination(@PathVariable Long id) {
        examinationService.deleteExamination(id);
        return ResponseEntity.ok(Collections.singletonMap("message", "Examination deleted successfully"));
    }
}
