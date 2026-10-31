package com.college.grademanagement.controller;

import com.college.grademanagement.dto.StudentResultDto;
import com.college.grademanagement.service.ResultService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/results")
public class ResultController {

    private final ResultService resultService;

    public ResultController(ResultService resultService) {
        this.resultService = resultService;
    }

    @GetMapping("/student/{rollNumber}")
    public ResponseEntity<StudentResultDto> getStudentResult(
            @PathVariable String rollNumber,
            @RequestParam(value = "examId", required = false) Long examId) {
        StudentResultDto result = resultService.getStudentResult(rollNumber, examId);
        return ResponseEntity.ok(result);
    }
}
