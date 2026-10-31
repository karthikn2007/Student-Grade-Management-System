package com.college.grademanagement.controller;

import com.college.grademanagement.dto.MarkDto;
import com.college.grademanagement.dto.MarkEntryRequest;
import com.college.grademanagement.service.MarkService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/marks")
public class MarkController {

    private final MarkService markService;

    public MarkController(MarkService markService) {
        this.markService = markService;
    }

    @GetMapping
    public ResponseEntity<List<MarkDto>> getAllMarks() {
        return ResponseEntity.ok(markService.getAllMarks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarkDto> getMarkById(@PathVariable Long id) {
        return ResponseEntity.ok(markService.getMarkById(id));
    }

    @GetMapping("/exam/{examId}")
    public ResponseEntity<List<MarkDto>> getMarksByExam(@PathVariable Long examId) {
        return ResponseEntity.ok(markService.getMarksByExamination(examId));
    }

    @GetMapping("/student/{studentId}/exam/{examId}")
    public ResponseEntity<List<MarkDto>> getMarksByStudentAndExam(
            @PathVariable Long studentId,
            @PathVariable Long examId) {
        return ResponseEntity.ok(markService.getMarksByStudentAndExamination(studentId, examId));
    }

    @PostMapping
    public ResponseEntity<MarkDto> enterOrUpdateMark(@Valid @RequestBody MarkEntryRequest request) {
        MarkDto saved = markService.enterOrUpdateMark(request);
        return new ResponseEntity<>(saved, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteMark(@PathVariable Long id) {
        markService.deleteMark(id);
        return ResponseEntity.ok(Collections.singletonMap("message", "Mark record deleted successfully"));
    }
}
