package com.quizshare.dto.response.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminSubjectItem {

    private Long id;
    private String title;
    private String description;
    private String image;

    @JsonProperty("department_id")
    private Long departmentId;

    @JsonProperty("department_title")
    private String departmentTitle;

    @JsonProperty("exam_count")
    private long examCount;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
