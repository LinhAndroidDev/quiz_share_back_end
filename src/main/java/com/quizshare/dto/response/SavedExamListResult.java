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
public class SavedExamListResult {

    @JsonProperty("department_id")
    private Long departmentId;

    @JsonProperty("department_title")
    private String departmentTitle;

    @JsonProperty("subject_title")
    private String subjectTitle;

    @JsonProperty("exam_list")
    private List<SavedExamItem> examList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SavedExamItem {
        private Long id;
        private String title;
        private String image;
        private Integer time;
        private Integer number;

        @JsonProperty("saved_num")
        private Integer savedNum;

        private String status;
    }
}
