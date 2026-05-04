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
public class ListExamResult {

    private Long id;
    private String title;
    private String description;

    @JsonProperty("department_id")
    private Long departmentId;

    @JsonProperty("department_title")
    private String departmentTitle;

    @JsonProperty("department_description")
    private String departmentDescription;

    @JsonProperty("list_exam")
    private List<ExamItem> listExam;
}
