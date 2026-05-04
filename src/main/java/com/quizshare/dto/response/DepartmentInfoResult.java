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
public class DepartmentInfoResult {

    private Long id;
    private String title;

    @JsonProperty("exam_num")
    private long examNum;

    private List<SubjectResult> subjects;
}
