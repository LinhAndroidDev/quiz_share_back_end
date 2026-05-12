package com.quizshare.dto.request.admin;

import com.quizshare.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserRoleRequest {

    @NotNull(message = "role is required")
    private User.Role role;
}
