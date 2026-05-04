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
public class ExamHistoryDetailResult {

    private Long id;

    @JsonProperty("exam_title")
    private String examTitle;

    private String description;

    @JsonProperty("subject_title")
    private String subjectTitle;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("user_avatar")
    private String userAvatar;

    private Integer time;
    private Integer number;
}
