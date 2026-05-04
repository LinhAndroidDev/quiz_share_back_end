package com.quizshare.controller;

import com.quizshare.dto.request.CreateExamRequest;
import com.quizshare.dto.request.ExamListQuestionRequest;
import com.quizshare.dto.request.ListExamRequest;
import com.quizshare.dto.response.BaseResponse;
import com.quizshare.dto.response.ExamQuestionResult;
import com.quizshare.dto.response.ListExamResult;
import com.quizshare.service.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @PostMapping("/listExam")
    public ResponseEntity<BaseResponse<ListExamResult>> listExam(
            @Valid @RequestBody ListExamRequest request) {
        ListExamResult result = examService.listExams(request);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @PostMapping("/examListQuestion")
    public ResponseEntity<BaseResponse<ExamQuestionResult>> examListQuestion(
            @Valid @RequestBody ExamListQuestionRequest request) {
        ExamQuestionResult result = examService.getExamQuestions(request.getExamId());
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @PostMapping("/createExam")
    public ResponseEntity<BaseResponse<String>> createExam(
            @Valid @RequestBody CreateExamRequest request) {
        String result = examService.createExam(request);
        return ResponseEntity.ok(BaseResponse.success("Created", result));
    }
}
