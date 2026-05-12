package com.quizshare.dto.request.admin;

import com.quizshare.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserStatusRequest {

    @NotNull(message = "status is required")
    private User.UserStatus status;
}
