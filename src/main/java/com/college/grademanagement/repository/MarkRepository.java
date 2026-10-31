package com.college.grademanagement.repository;

import com.college.grademanagement.model.Mark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarkRepository extends JpaRepository<Mark, Long> {

    List<Mark> findByStudentId(Long studentId);

    List<Mark> findByExaminationId(Long examinationId);

    List<Mark> findBySubjectId(Long subjectId);

    List<Mark> findByStudentIdAndExaminationId(Long studentId, Long examinationId);

    List<Mark> findBySubjectIdAndExaminationId(Long subjectId, Long examinationId);

    Optional<Mark> findByStudentIdAndSubjectIdAndExaminationId(Long studentId, Long subjectId, Long examinationId);

    boolean existsByStudentIdAndSubjectIdAndExaminationId(Long studentId, Long subjectId, Long examinationId);

    @Query("SELECT m FROM Mark m WHERE m.student.rollNumber = :rollNumber AND m.examination.id = :examinationId")
    List<Mark> findByStudentRollNumberAndExaminationId(@Param("rollNumber") String rollNumber, @Param("examinationId") Long examinationId);

    @Query("SELECT m FROM Mark m WHERE LOWER(m.student.rollNumber) = LOWER(:rollNumber)")
    List<Mark> findByStudentRollNumberIgnoreCase(@Param("rollNumber") String rollNumber);
}
