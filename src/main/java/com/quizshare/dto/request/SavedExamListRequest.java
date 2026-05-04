package com.quizshare.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SavedExamListRequest {

    @NotNull(message = "user_id is required")
    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("subject_id")
    private Long subjectId;

    private String type = "ALL";

    @JsonProperty("sort_field")
    private String sortField = "created_at";

    @JsonProperty("sort_by")
    private String sortBy = "DESC";
}
