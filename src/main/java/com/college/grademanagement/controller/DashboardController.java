package com.college.grademanagement.controller;

import com.college.grademanagement.dto.DashboardStatsDto;
import com.college.grademanagement.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDto> getDashboardStats(
            @RequestParam(value = "examId", required = false) Long examId) {
        DashboardStatsDto stats = dashboardService.getDashboardStats(examId);
        return ResponseEntity.ok(stats);
    }
}
