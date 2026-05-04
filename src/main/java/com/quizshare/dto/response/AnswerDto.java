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
public class AnswerDto {

    @JsonProperty("answer_id")
    private Long answerId;

    private String content;
    private String image;
    private Integer sort;
    private Integer type;
}
