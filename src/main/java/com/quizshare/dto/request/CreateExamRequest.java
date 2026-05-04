package com.quizshare.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateExamRequest {

    @NotNull(message = "user_id is required")
    @JsonProperty("user_id")
    private Long userId;

    @NotNull(message = "subject_id is required")
    @JsonProperty("subject_id")
    private Long subjectId;

    @NotBlank(message = "title is required")
    private String title;

    private String description;

    @NotNull(message = "time is required")
    private Integer time;

    @NotNull(message = "number is required")
    private Integer number;

    private String status = "PUBLIC";

    @JsonProperty("question_exam_list")
    private List<QuestionRequest> questionExamList;

    @Data
    public static class QuestionRequest {

        @JsonProperty("question_title")
        private String questionTitle;

        @JsonProperty("question_image")
        private String questionImage;

        @JsonProperty("question_image_url")
        private String questionImageUrl;

        @JsonProperty("question_level")
        private String questionLevel;

        @JsonProperty("question_sort")
        private Integer questionSort;

        @JsonProperty("answer_list")
        private List<AnswerRequest> answerList;
    }

    @Data
    public static class AnswerRequest {
        private String content;
        private String image;
        private Integer sort;
        private Integer type;
    }
}
