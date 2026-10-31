package com.college.grademanagement.repository;

import com.college.grademanagement.model.Examination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExaminationRepository extends JpaRepository<Examination, Long> {

    Optional<Examination> findByExamCode(String examCode);

    boolean existsByExamCode(String examCode);

    List<Examination> findByAcademicYear(String academicYear);

    List<Examination> findBySemester(String semester);
}
