package com.quizshare.controller.admin;

import com.quizshare.dto.request.admin.UpdateExamStatusRequest;
import com.quizshare.dto.response.BaseResponse;
import com.quizshare.dto.response.admin.AdminExamDetailResult;
import com.quizshare.dto.response.admin.AdminExamItem;
import com.quizshare.dto.response.admin.AdminPageResult;
import com.quizshare.service.admin.AdminExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/exams")
@RequiredArgsConstructor
public class AdminExamController {

    private final AdminExamService adminExamService;

    @GetMapping
    public ResponseEntity<BaseResponse<AdminPageResult<AdminExamItem>>> getExams(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false, defaultValue = "") String status,
            @RequestParam(name = "subject_id", required = false) Long subjectId,
            @RequestParam(name = "department_id", required = false) Long departmentId) {
        return ResponseEntity.ok(BaseResponse.success(
                adminExamService.getExams(page, size, keyword, status, subjectId, departmentId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<AdminExamDetailResult>> getExamDetail(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponse.success(adminExamService.getExamDetail(id)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BaseResponse<Boolean>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateExamStatusRequest request) {
        return ResponseEntity.ok(BaseResponse.success("Updated",
                adminExamService.updateStatus(id, request.getStatus())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Boolean>> deleteExam(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponse.success("Deleted",
                adminExamService.deleteExam(id)));
    }
}
