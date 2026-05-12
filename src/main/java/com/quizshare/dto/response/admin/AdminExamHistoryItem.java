package com.quizshare.dto.response.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class AdminExamHistoryItem {

    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("user_email")
    private String userEmail;

    @JsonProperty("exam_id")
    private Long examId;

    @JsonProperty("exam_title")
    private String examTitle;

    private BigDecimal score;

    @JsonProperty("correct_number")
    private Integer correctNumber;

    @JsonProperty("wrong_number")
    private Integer wrongNumber;

    @JsonProperty("skip_number")
    private Integer skipNumber;

    @JsonProperty("start_time")
    private LocalDateTime startTime;

    @JsonProperty("finish_time")
    private LocalDateTime finishTime;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
