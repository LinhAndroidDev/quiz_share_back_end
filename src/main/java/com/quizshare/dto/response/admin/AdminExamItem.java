package com.quizshare.dto.response.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quizshare.entity.Exam;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminExamItem {

    private Long id;
    private String title;
    private String image;

    @JsonProperty("subject_id")
    private Long subjectId;

    @JsonProperty("subject_title")
    private String subjectTitle;

    @JsonProperty("department_id")
    private Long departmentId;

    @JsonProperty("department_title")
    private String departmentTitle;

    @JsonProperty("author_id")
    private Long authorId;

    @JsonProperty("author_name")
    private String authorName;

    @JsonProperty("author_email")
    private String authorEmail;

    private Integer time;
    private Integer number;

    @JsonProperty("saved_num")
    private Integer savedNum;

    private Exam.ExamStatus status;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
