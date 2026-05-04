package com.quizshare.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class SubmitExamRequest {

    @NotNull(message = "user_id is required")
    @JsonProperty("user_id")
    private Long userId;

    @NotNull(message = "exam_id is required")
    @JsonProperty("exam_id")
    private Long examId;

    /**
     * Map of question_id -> answer_id (null = skipped)
     */
    @JsonProperty("answer_list")
    private Map<String, Long> answerList;

    @JsonProperty("start_time")
    private String startTime;

    @JsonProperty("finish_time")
    private String finishTime;
}
