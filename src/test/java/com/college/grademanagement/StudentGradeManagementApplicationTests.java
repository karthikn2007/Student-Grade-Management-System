package com.college.grademanagement;

import com.college.grademanagement.dto.MarkEntryRequest;
import com.college.grademanagement.dto.StudentResultDto;
import com.college.grademanagement.exception.BadRequestException;
import com.college.grademanagement.service.MarkService;
import com.college.grademanagement.service.ResultService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StudentGradeManagementApplicationTests {

    @Autowired
    private MarkService markService;

    @Autowired
    private ResultService resultService;

    @Test
    void contextLoads() {
        assertNotNull(markService);
        assertNotNull(resultService);
    }

    @Test
    @DisplayName("Verify Academic Grade Scale (A+ through F)")
    void testGradeCalculationScale() {
        assertEquals("A+", MarkService.calculateGrade(95.0, 100.0));
        assertEquals("A+", MarkService.calculateGrade(90.0, 100.0));
        assertEquals("A", MarkService.calculateGrade(89.5, 100.0));
        assertEquals("A", MarkService.calculateGrade(80.0, 100.0));
        assertEquals("B", MarkService.calculateGrade(79.0, 100.0));
        assertEquals("B", MarkService.calculateGrade(70.0, 100.0));
        assertEquals("C", MarkService.calculateGrade(69.0, 100.0));
        assertEquals("C", MarkService.calculateGrade(60.0, 100.0));
        assertEquals("D", MarkService.calculateGrade(59.0, 100.0));
        assertEquals("D", MarkService.calculateGrade(50.0, 100.0));
        assertEquals("E", MarkService.calculateGrade(49.0, 100.0));
        assertEquals("E", MarkService.calculateGrade(40.0, 100.0));
        assertEquals("F", MarkService.calculateGrade(39.9, 100.0));
        assertEquals("F", MarkService.calculateGrade(0.0, 100.0));
    }

    @Test
    @DisplayName("Verify Subject Passing Marks Status")
    void testSubjectPassFailStatus() {
        assertEquals("PASS", MarkService.determineStatus(40.0, 40.0));
        assertEquals("PASS", MarkService.determineStatus(95.0, 40.0));
        assertEquals("FAIL", MarkService.determineStatus(39.5, 40.0));
        assertEquals("FAIL", MarkService.determineStatus(0.0, 40.0));
    }

    @Test
    @DisplayName("Verify Marks Boundary Enforcement (Reject negative & exceeding max marks)")
    void testMarksBoundaryValidation() {
        MarkEntryRequest negativeMarks = new MarkEntryRequest(1L, 1L, 1L, -5.0, "Test negative");
        assertThrows(BadRequestException.class, () -> markService.enterOrUpdateMark(negativeMarks));

        MarkEntryRequest exceedingMarks = new MarkEntryRequest(1L, 1L, 1L, 105.0, "Test exceeding");
        assertThrows(BadRequestException.class, () -> markService.enterOrUpdateMark(exceedingMarks));
    }

    @Test
    @DisplayName("Verify Overall Examination Pass Rule: Student passes only if every subject is passed")
    void testOverallExaminationPassFailEvaluation() {
        // Student 1 (Alex Turner) passed all 5 subjects
        StudentResultDto passResult = resultService.getStudentResult("CS2024001", 1L);
        assertEquals("PASS", passResult.getOverallStatus());
        assertEquals("A+", passResult.getOverallGrade());
        assertEquals(5, passResult.getPassedSubjectsCount());
        assertEquals(0, passResult.getFailedSubjectsCount());

        // Student 8 (Isabella Martinez) failed Subject 2 (Algorithms: 34/100)
        StudentResultDto failResult = resultService.getStudentResult("EC2024008", 1L);
        assertEquals("FAIL", failResult.getOverallStatus());
        assertEquals(1, failResult.getFailedSubjectsCount());
        assertEquals(4, failResult.getPassedSubjectsCount());
    }
}
