package com.quizshare.dto.response.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quizshare.entity.Exam;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AdminExamDetailResult {

    private Long id;
    private String title;
    private String description;
    private String image;

    @JsonProperty("subject_title")
    private String subjectTitle;

    @JsonProperty("department_title")
    private String departmentTitle;

    @JsonProperty("author_name")
    private String authorName;

    private Integer time;
    private Integer number;
    private Exam.ExamStatus status;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    private List<QuestionItem> questions;

    @Data
    @Builder
    public static class QuestionItem {
        private Long id;

        @JsonProperty("question_title")
        private String questionTitle;

        @JsonProperty("question_image")
        private String questionImage;

        @JsonProperty("question_level")
        private String questionLevel;

        @JsonProperty("question_sort")
        private Integer questionSort;

        private List<AnswerItem> answers;
    }

    @Data
    @Builder
    public static class AnswerItem {
        private Long id;
        private String content;
        private String image;
        private Integer sort;
        private Integer type;
    }
}
