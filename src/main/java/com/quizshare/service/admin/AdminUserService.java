package com.quizshare.service.admin;

import com.quizshare.dto.response.admin.AdminPageResult;
import com.quizshare.dto.response.admin.AdminUserDetailResult;
import com.quizshare.dto.response.admin.AdminUserItem;
import com.quizshare.entity.User;
import com.quizshare.exception.AppException;
import com.quizshare.exception.ErrorCode;
import com.quizshare.repository.ExamHistoryRepository;
import com.quizshare.repository.ExamRepository;
import com.quizshare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final ExamHistoryRepository examHistoryRepository;

    public AdminPageResult<AdminUserItem> getUsers(int page, int size,
                                                   String keyword,
                                                   String status,
                                                   String role) {
        User.UserStatus statusEnum = parseEnum(User.UserStatus.class, status);
        User.Role roleEnum = parseEnum(User.Role.class, role);

        Page<User> userPage = userRepository.searchUsers(keyword, statusEnum, roleEnum,
                PageRequest.of(page, size));

        List<AdminUserItem> items = userPage.getContent().stream()
                .map(this::toItem)
                .collect(Collectors.toList());

        return AdminPageResult.<AdminUserItem>builder()
                .total(userPage.getTotalElements())
                .page(page)
                .size(size)
                .items(items)
                .build();
    }

    public AdminUserDetailResult getUserDetail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        long totalExamsCreated = examRepository.countByAuthorId(userId);
        long totalExamsTaken = examHistoryRepository.countByUserId(userId);

        return AdminUserDetailResult.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .birthday(user.getBirthday())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .totalExamsCreated(totalExamsCreated)
                .totalExamsTaken(totalExamsTaken)
                .build();
    }

    public boolean updateStatus(Long userId, User.UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setStatus(status);
        userRepository.save(user);
        return true;
    }

    public boolean updateRole(Long userId, User.Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setRole(role);
        userRepository.save(user);
        return true;
    }

    private AdminUserItem toItem(User user) {
        return AdminUserItem.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .birthday(user.getBirthday())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private <E extends Enum<E>> E parseEnum(Class<E> enumClass, String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
