package com.quizshare.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {

    @JsonProperty("question_id")
    private Long questionId;

    @JsonProperty("question_title")
    private String questionTitle;

    @JsonProperty("question_image")
    private String questionImage;

    @JsonProperty("question_level")
    private String questionLevel;

    @JsonProperty("question_sort")
    private Integer questionSort;

    @JsonProperty("answer_list")
    private List<AnswerDto> answerList;
}
