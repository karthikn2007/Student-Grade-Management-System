package com.college.grademanagement.service;

import com.college.grademanagement.dto.StudentDto;
import com.college.grademanagement.exception.DuplicateResourceException;
import com.college.grademanagement.exception.ResourceNotFoundException;
import com.college.grademanagement.model.Student;
import com.college.grademanagement.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<StudentDto> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public StudentDto getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
        return toDto(student);
    }

    public StudentDto getStudentByRollNumber(String rollNumber) {
        Student student = studentRepository.findByRollNumberIgnoreCase(rollNumber.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with Roll Number: " + rollNumber));
        return toDto(student);
    }

    public List<StudentDto> searchStudents(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllStudents();
        }
        return studentRepository.searchStudents(query.trim()).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public StudentDto createStudent(StudentDto dto) {
        if (studentRepository.existsByRollNumber(dto.getRollNumber().trim())) {
            throw new DuplicateResourceException("Roll number already exists: " + dto.getRollNumber());
        }
        if (studentRepository.existsByEmail(dto.getEmail().trim())) {
            throw new DuplicateResourceException("Student email already exists: " + dto.getEmail());
        }

        Student student = new Student(
                dto.getRollNumber().trim().toUpperCase(),
                dto.getFullName().trim(),
                dto.getEmail().trim().toLowerCase(),
                dto.getPhoneNumber().trim(),
                dto.getDepartment().trim(),
                dto.getAcademicYear().trim(),
                dto.getSemester().trim(),
                dto.getDateOfBirth()
        );

        Student saved = studentRepository.save(student);
        return toDto(saved);
    }

    @Transactional
    public StudentDto updateStudent(Long id, StudentDto dto) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        String newRoll = dto.getRollNumber().trim().toUpperCase();
        if (!student.getRollNumber().equalsIgnoreCase(newRoll) && studentRepository.existsByRollNumber(newRoll)) {
            throw new DuplicateResourceException("Roll number already in use by another student: " + newRoll);
        }

        String newEmail = dto.getEmail().trim().toLowerCase();
        if (!student.getEmail().equalsIgnoreCase(newEmail) && studentRepository.existsByEmail(newEmail)) {
            throw new DuplicateResourceException("Email already in use by another student: " + newEmail);
        }

        student.setRollNumber(newRoll);
        student.setFullName(dto.getFullName().trim());
        student.setEmail(newEmail);
        student.setPhoneNumber(dto.getPhoneNumber().trim());
        student.setDepartment(dto.getDepartment().trim());
        student.setAcademicYear(dto.getAcademicYear().trim());
        student.setSemester(dto.getSemester().trim());
        student.setDateOfBirth(dto.getDateOfBirth());

        Student updated = studentRepository.save(student);
        return toDto(updated);
    }

    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with ID: " + id);
        }
        studentRepository.deleteById(id);
    }

    public StudentDto toDto(Student student) {
        return new StudentDto(
                student.getId(),
                student.getRollNumber(),
                student.getFullName(),
                student.getEmail(),
                student.getPhoneNumber(),
                student.getDepartment(),
                student.getAcademicYear(),
                student.getSemester(),
                student.getDateOfBirth()
        );
    }
}
