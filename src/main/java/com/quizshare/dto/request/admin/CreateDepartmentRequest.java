package com.quizshare.dto.request.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateDepartmentRequest {

    @NotBlank(message = "title is required")
    private String title;

    private String description;

    private String image;
}
