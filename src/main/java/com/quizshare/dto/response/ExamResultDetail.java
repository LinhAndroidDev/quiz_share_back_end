package com.quizshare.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamResultDetail {

    private Long id;

    @JsonProperty("exam_id")
    private Long examId;

    @JsonProperty("user_id")
    private Long userId;

    private BigDecimal score;

    @JsonProperty("correct_number")
    private Integer correctNumber;

    @JsonProperty("wrong_number")
    private Integer wrongNumber;

    @JsonProperty("skip_number")
    private Integer skipNumber;

    @JsonProperty("start_time")
    private String startTime;

    @JsonProperty("finish_time")
    private String finishTime;

    @JsonProperty("exam_result")
    private Map<String, Long> examResult;

    @JsonProperty("create_at")
    private String createAt;

    @JsonProperty("update_at")
    private String updateAt;

    @JsonProperty("delete_at")
    private String deleteAt;
}
