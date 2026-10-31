package com.college.grademanagement.repository;

import com.college.grademanagement.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByEmployeeId(String employeeId);
    boolean existsByEmployeeId(String employeeId);
    boolean existsByEmail(String email);
    List<Teacher> findByDepartment(String department);
}
