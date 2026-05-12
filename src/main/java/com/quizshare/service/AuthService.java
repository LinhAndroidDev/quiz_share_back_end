package com.quizshare.service;

import com.quizshare.dto.request.ForgotPasswordRequest;
import com.quizshare.dto.request.LoginRequest;
import com.quizshare.dto.request.RegisterRequest;
import com.quizshare.dto.response.LoginResult;
import com.quizshare.entity.User;
import com.quizshare.exception.AppException;
import com.quizshare.exception.ErrorCode;
import com.quizshare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JavaMailSender mailSender;

    @Value("${app.mail.from:noreply@quizshare.com}")
    private String mailFrom;

    public LoginResult register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()
                && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AppException(ErrorCode.PHONE_ALREADY_EXISTS);
        }

        User.Role role = request.getRole() != null ? request.getRole() : User.Role.USER;

        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .birthday(parseBirthday(request.getBirthday()))
                .role(role)
                .build();

        user = userRepository.save(user);

        String token = jwtService.generateToken(user);
        return LoginResult.builder()
                .accessToken(token)
                .userId(user.getId())
                .build();
    }

    public LoginResult login(LoginRequest request) {
        User user = userRepository
                .findByEmailOrPhoneNumber(request.getLoginId(), request.getLoginId())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        if (user.getStatus() == User.UserStatus.BANNED) {
            throw new AppException(ErrorCode.USER_BANNED);
        }
        if (user.getStatus() == User.UserStatus.INACTIVE) {
            throw new AppException(ErrorCode.USER_INACTIVE);
        }

        String token = jwtService.generateToken(user);
        return LoginResult.builder()
                .accessToken(token)
                .userId(user.getId())
                .build();
    }

    public boolean forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String tempPassword = generateTempPassword();
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);

        sendPasswordResetEmail(user.getEmail(), tempPassword);
        return true;
    }

    private void sendPasswordResetEmail(String email, String tempPassword) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(email);
            message.setSubject("QuizShare - Password Reset");
            message.setText("Your temporary password is: " + tempPassword
                    + "\nPlease login and change your password immediately.");
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", email, e.getMessage());
        }
    }

    private String generateTempPassword() {
        return "Temp" + (int) (Math.random() * 100000);
    }

    private LocalDate parseBirthday(String birthday) {
        if (birthday == null || birthday.isBlank()) return null;
        try {
            return LocalDate.parse(birthday);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
