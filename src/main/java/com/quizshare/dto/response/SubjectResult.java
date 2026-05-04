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
public class SubjectResult {

    private Long id;
    private String title;
    private String description;
    private String image;

    @JsonProperty("count_exam")
    private long countExam;

    @JsonProperty("department_id")
    private Long departmentId;
}
