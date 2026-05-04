package com.quizshare.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveExamResult {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("exam_id")
    private Long examId;

    @JsonProperty("create_at")
    private String createAt;
}
