package com.college.grademanagement.service;

import com.college.grademanagement.dto.LoginRequest;
import com.college.grademanagement.dto.LoginResponse;
import com.college.grademanagement.dto.UserDto;
import com.college.grademanagement.exception.BadRequestException;
import com.college.grademanagement.model.User;
import com.college.grademanagement.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    public static final String SESSION_USER_KEY = "CURRENT_USER";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request, HttpSession session) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty() ||
            request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new BadRequestException("Username and password must not be empty");
        }

        User user = userRepository.findByUsername(request.getUsername().trim())
                .orElseThrow(() -> new BadRequestException("Invalid username or password"));

        if (!user.isEnabled()) {
            throw new BadRequestException("User account is disabled");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid username or password");
        }

        UserDto userDto = new UserDto(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.isEnabled()
        );

        session.setAttribute(SESSION_USER_KEY, userDto);

        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                "Login successful"
        );
    }

    public void logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }

    public Optional<UserDto> getCurrentUser(HttpSession session) {
        if (session == null) {
            return Optional.empty();
        }
        Object userObj = session.getAttribute(SESSION_USER_KEY);
        if (userObj instanceof UserDto) {
            return Optional.of((UserDto) userObj);
        }
        return Optional.empty();
    }
}
