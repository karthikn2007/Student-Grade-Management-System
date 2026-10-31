package com.college.grademanagement.service;

import com.college.grademanagement.dto.TeacherDto;
import com.college.grademanagement.exception.DuplicateResourceException;
import com.college.grademanagement.exception.ResourceNotFoundException;
import com.college.grademanagement.model.Teacher;
import com.college.grademanagement.model.User;
import com.college.grademanagement.repository.TeacherRepository;
import com.college.grademanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;

    public TeacherService(TeacherRepository teacherRepository, UserRepository userRepository) {
        this.teacherRepository = teacherRepository;
        this.userRepository = userRepository;
    }

    public List<TeacherDto> getAllTeachers() {
        return teacherRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public TeacherDto getTeacherById(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with ID: " + id));
        return toDto(teacher);
    }

    @Transactional
    public TeacherDto createTeacher(TeacherDto dto) {
        if (teacherRepository.existsByEmployeeId(dto.getEmployeeId().trim())) {
            throw new DuplicateResourceException("Employee ID already exists: " + dto.getEmployeeId());
        }
        if (teacherRepository.existsByEmail(dto.getEmail().trim())) {
            throw new DuplicateResourceException("Teacher email already exists: " + dto.getEmail());
        }

        User user = null;
        if (dto.getUsername() != null && !dto.getUsername().trim().isEmpty()) {
            user = userRepository.findByUsername(dto.getUsername().trim()).orElse(null);
        }

        Teacher teacher = new Teacher(
                dto.getEmployeeId().trim().toUpperCase(),
                dto.getFullName().trim(),
                dto.getEmail().trim().toLowerCase(),
                dto.getPhoneNumber() != null ? dto.getPhoneNumber().trim() : null,
                dto.getDepartment().trim(),
                dto.getDesignation().trim(),
                user
        );

        Teacher saved = teacherRepository.save(teacher);
        return toDto(saved);
    }

    @Transactional
    public TeacherDto updateTeacher(Long id, TeacherDto dto) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with ID: " + id));

        String newEmpId = dto.getEmployeeId().trim().toUpperCase();
        if (!teacher.getEmployeeId().equalsIgnoreCase(newEmpId) && teacherRepository.existsByEmployeeId(newEmpId)) {
            throw new DuplicateResourceException("Employee ID already in use: " + newEmpId);
        }

        String newEmail = dto.getEmail().trim().toLowerCase();
        if (!teacher.getEmail().equalsIgnoreCase(newEmail) && teacherRepository.existsByEmail(newEmail)) {
            throw new DuplicateResourceException("Email already in use: " + newEmail);
        }

        teacher.setEmployeeId(newEmpId);
        teacher.setFullName(dto.getFullName().trim());
        teacher.setEmail(newEmail);
        teacher.setPhoneNumber(dto.getPhoneNumber() != null ? dto.getPhoneNumber().trim() : null);
        teacher.setDepartment(dto.getDepartment().trim());
        teacher.setDesignation(dto.getDesignation().trim());

        Teacher updated = teacherRepository.save(teacher);
        return toDto(updated);
    }

    @Transactional
    public void deleteTeacher(Long id) {
        if (!teacherRepository.existsById(id)) {
            throw new ResourceNotFoundException("Teacher not found with ID: " + id);
        }
        teacherRepository.deleteById(id);
    }

    public TeacherDto toDto(Teacher teacher) {
        String username = (teacher.getUser() != null) ? teacher.getUser().getUsername() : null;
        return new TeacherDto(
                teacher.getId(),
                teacher.getEmployeeId(),
                teacher.getFullName(),
                teacher.getEmail(),
                teacher.getPhoneNumber(),
                teacher.getDepartment(),
                teacher.getDesignation(),
                username
        );
    }
}
