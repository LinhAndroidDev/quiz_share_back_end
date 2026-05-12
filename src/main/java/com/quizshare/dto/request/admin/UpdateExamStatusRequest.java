package com.quizshare.dto.request.admin;

import com.quizshare.entity.Exam;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateExamStatusRequest {

    @NotNull(message = "status is required")
    private Exam.ExamStatus status;
}
