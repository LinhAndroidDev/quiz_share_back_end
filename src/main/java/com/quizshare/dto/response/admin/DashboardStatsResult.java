package com.quizshare.dto.response.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStatsResult {

    @JsonProperty("total_users")
    private long totalUsers;

    @JsonProperty("active_users")
    private long activeUsers;

    @JsonProperty("inactive_users")
    private long inactiveUsers;

    @JsonProperty("banned_users")
    private long bannedUsers;

    @JsonProperty("total_exams")
    private long totalExams;

    @JsonProperty("public_exams")
    private long publicExams;

    @JsonProperty("private_exams")
    private long privateExams;

    @JsonProperty("draft_exams")
    private long draftExams;

    @JsonProperty("total_exam_histories")
    private long totalExamHistories;

    @JsonProperty("total_departments")
    private long totalDepartments;

    @JsonProperty("total_subjects")
    private long totalSubjects;
}
