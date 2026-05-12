package com.quizshare.controller.admin;

import com.quizshare.dto.response.BaseResponse;
import com.quizshare.dto.response.admin.AdminExamHistoryItem;
import com.quizshare.dto.response.admin.AdminPageResult;
import com.quizshare.service.admin.AdminExamHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/exam-histories")
@RequiredArgsConstructor
public class AdminExamHistoryController {

    private final AdminExamHistoryService adminExamHistoryService;

    @GetMapping
    public ResponseEntity<BaseResponse<AdminPageResult<AdminExamHistoryItem>>> getHistories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(name = "user_id", required = false) Long userId,
            @RequestParam(name = "exam_id", required = false) Long examId,
            @RequestParam(required = false, defaultValue = "") String from,
            @RequestParam(required = false, defaultValue = "") String to) {
        return ResponseEntity.ok(BaseResponse.success(
                adminExamHistoryService.getHistories(page, size, userId, examId, from, to)));
    }
}
