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
public class ExamItem {

    private Long id;
    private String title;
    private String image;
    private Integer time;
    private Integer number;

    @JsonProperty("saved_num")
    private Integer savedNum;

    private String status;

    @JsonProperty("author_id")
    private Long authorId;

    @JsonProperty("author_name")
    private String authorName;

    @JsonProperty("author_email")
    private String authorEmail;
}
