package com.quizshare.dto.request.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateSubjectRequest {

    @NotNull(message = "department_id is required")
    @JsonProperty("department_id")
    private Long departmentId;

    @NotBlank(message = "title is required")
    private String title;

    private String description;

    private String image;
}
