package com.quizshare.dto.response.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminDepartmentItem {

    private Long id;
    private String title;
    private String description;
    private String image;

    @JsonProperty("subject_count")
    private long subjectCount;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
