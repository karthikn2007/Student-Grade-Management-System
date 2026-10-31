package com.college.grademanagement.service;

import com.college.grademanagement.dto.DashboardStatsDto;
import com.college.grademanagement.dto.StudentResultDto;
import com.college.grademanagement.model.Examination;
import com.college.grademanagement.model.Student;
import com.college.grademanagement.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DashboardService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final ExaminationRepository examinationRepository;
    private final MarkRepository markRepository;
    private final ResultService resultService;

    public DashboardService(StudentRepository studentRepository,
                            TeacherRepository teacherRepository,
                            SubjectRepository subjectRepository,
                            ExaminationRepository examinationRepository,
                            MarkRepository markRepository,
                            ResultService resultService) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
        this.examinationRepository = examinationRepository;
        this.markRepository = markRepository;
        this.resultService = resultService;
    }

    public DashboardStatsDto getDashboardStats(Long examinationId) {
        long totalStudents = studentRepository.count();
        long totalTeachers = teacherRepository.count();
        long totalSubjects = subjectRepository.count();
        long totalExaminations = examinationRepository.count();

        // Determine target examination
        Long targetExamId = examinationId;
        if (targetExamId == null) {
            List<Examination> exams = examinationRepository.findAll();
            if (!exams.isEmpty()) {
                targetExamId = exams.get(0).getId();
            }
        }

        long passedCount = 0;
        long failedCount = 0;
        double sumPercentages = 0.0;
        int evaluatedStudentsCount = 0;

        Map<String, Long> gradeDist = new LinkedHashMap<>();
        gradeDist.put("A+", 0L);
        gradeDist.put("A", 0L);
        gradeDist.put("B", 0L);
        gradeDist.put("C", 0L);
        gradeDist.put("D", 0L);
        gradeDist.put("E", 0L);
        gradeDist.put("F", 0L);

        Map<String, List<Double>> deptPercentages = new HashMap<>();

        if (targetExamId != null) {
            List<Student> allStudents = studentRepository.findAll();

            for (Student student : allStudents) {
                try {
                    StudentResultDto result = resultService.getStudentResult(student.getRollNumber(), targetExamId);
                    if (result.getTotalSubjects() > 0) {
                        evaluatedStudentsCount++;
                        sumPercentages += result.getPercentage();

                        if ("PASS".equalsIgnoreCase(result.getOverallStatus())) {
                            passedCount++;
                        } else {
                            failedCount++;
                        }

                        // Grade distribution count
                        String grade = result.getOverallGrade();
                        if (grade != null && gradeDist.containsKey(grade)) {
                            gradeDist.put(grade, gradeDist.get(grade) + 1L);
                        } else if (grade != null) {
                            gradeDist.put(grade, 1L);
                        }

                        // Department grouping
                        String dept = student.getDepartment();
                        deptPercentages.computeIfAbsent(dept, k -> new ArrayList<>()).add(result.getPercentage());
                    }
                } catch (Exception ignored) {
                    // Skip un-evaluated students
                }
            }
        }

        double avgPercentage = (evaluatedStudentsCount > 0)
                ? Math.round((sumPercentages / evaluatedStudentsCount) * 100.0) / 100.0
                : 0.0;

        Map<String, Double> deptAverages = new LinkedHashMap<>();
        for (Map.Entry<String, List<Double>> entry : deptPercentages.entrySet()) {
            double avg = entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            deptAverages.put(entry.getKey(), Math.round(avg * 100.0) / 100.0);
        }

        DashboardStatsDto stats = new DashboardStatsDto(
                totalStudents,
                totalTeachers,
                totalSubjects,
                totalExaminations,
                avgPercentage,
                passedCount,
                failedCount
        );
        stats.setGradeDistribution(gradeDist);
        stats.setDepartmentPerformance(deptAverages);

        return stats;
    }
}
