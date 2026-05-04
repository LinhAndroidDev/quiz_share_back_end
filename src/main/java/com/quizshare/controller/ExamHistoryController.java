package com.quizshare.controller;

import com.quizshare.dto.request.GetExamHistoryListRequest;
import com.quizshare.dto.request.GetExamResultRequest;
import com.quizshare.dto.request.SubmitExamRequest;
import com.quizshare.dto.response.*;
import com.quizshare.service.ExamHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ExamHistoryController {

    private final ExamHistoryService examHistoryService;

    @PostMapping("/submitExam")
    public ResponseEntity<BaseResponse<SubmitExamResult>> submitExam(
            @Valid @RequestBody SubmitExamRequest request) {
        SubmitExamResult result = examHistoryService.submitExam(request);
        return ResponseEntity.ok(BaseResponse.success("Submitted", result));
    }

    @PostMapping("/getExamHistoryList")
    public ResponseEntity<BaseResponse<List<ExamHistoryItem>>> getExamHistoryList(
            @Valid @RequestBody GetExamHistoryListRequest request) {
        List<ExamHistoryItem> result = examHistoryService.getExamHistoryList(request);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @GetMapping("/getExamHistoryDetail")
    public ResponseEntity<BaseResponse<ExamHistoryDetailResult>> getExamHistoryDetail(
            @RequestParam("user_id") Long userId,
            @RequestParam("exam_history_id") Long examHistoryId) {
        ExamHistoryDetailResult result = examHistoryService.getExamHistoryDetail(examHistoryId);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @PostMapping("/getExamResult")
    public ResponseEntity<BaseResponse<ExamResultDetail>> getExamResult(
            @Valid @RequestBody GetExamResultRequest request) {
        ExamResultDetail result = examHistoryService.getExamResult(request.getExamHistoryId());
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
