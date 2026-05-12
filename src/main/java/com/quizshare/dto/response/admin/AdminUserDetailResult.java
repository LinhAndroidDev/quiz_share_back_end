package com.quizshare.dto.response.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quizshare.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class AdminUserDetailResult {

    private Long id;
    private String name;
    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    private LocalDate birthday;
    private String avatar;
    private User.Role role;
    private User.UserStatus status;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("total_exams_created")
    private long totalExamsCreated;

    @JsonProperty("total_exams_taken")
    private long totalExamsTaken;
}
