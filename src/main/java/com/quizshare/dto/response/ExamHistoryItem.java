package com.quizshare.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamHistoryItem {

    @JsonProperty("exam_history_id")
    private Long examHistoryId;

    private String title;
    private Integer number;

    @JsonProperty("user_create")
    private String userCreate;

    private String image;
    private BigDecimal score;
}
