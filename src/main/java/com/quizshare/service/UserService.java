package com.quizshare.service;

import com.quizshare.dto.request.*;
import com.quizshare.dto.response.UserResult;
import com.quizshare.entity.User;
import com.quizshare.exception.AppException;
import com.quizshare.exception.ErrorCode;
import com.quizshare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final FileStorageService fileStorageService;

    public UserResult getUserInfo(Long userId) {
        User user = getUserById(userId);
        String token = jwtService.generateToken(user);
        return mapToUserResult(user, token);
    }

    public String updateUserInfo(UpdateUserInfoRequest request) {
        User user = getUserById(request.getUserId());
        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }
        if (request.getBirthday() != null && !request.getBirthday().isBlank()) {
            user.setBirthday(parseBirthday(request.getBirthday()));
        }
        userRepository.save(user);
        return "Update successful";
    }

    public String changeEmail(ChangeEmailRequest request) {
        User user = getUserById(request.getUserId());
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        user.setEmail(request.getEmail());
        userRepository.save(user);
        return "Email updated";
    }

    public boolean changePassword(ChangePasswordRequest request) {
        if (!request.getPassword().equals(request.getCfPassword())) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }
        User user = getUserById(request.getUserId());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return true;
    }

    public boolean editAvatar(Long userId, MultipartFile file) {
        User user = getUserById(userId);
        String avatarUrl = fileStorageService.uploadFile(file, "avatars",
                "avatar_" + userId);
        user.setAvatar(avatarUrl);
        userRepository.save(user);
        return true;
    }

    public String unpublicUser(Long userId) {
        User user = getUserById(userId);
        user.setStatus(User.UserStatus.INACTIVE);
        userRepository.save(user);
        return "Deactivated";
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private UserResult mapToUserResult(User user, String token) {
        return UserResult.builder()
                .accessToken(token)
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .birthday(user.getBirthday() != null ? user.getBirthday().toString() : null)
                .avatar(user.getAvatar())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .build();
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
