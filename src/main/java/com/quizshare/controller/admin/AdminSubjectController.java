package com.quizshare.controller.admin;

import com.quizshare.dto.request.admin.CreateSubjectRequest;
import com.quizshare.dto.request.admin.UpdateSubjectRequest;
import com.quizshare.dto.response.BaseResponse;
import com.quizshare.dto.response.admin.AdminPageResult;
import com.quizshare.dto.response.admin.AdminSubjectItem;
import com.quizshare.dto.response.admin.CreatedIdResult;
import com.quizshare.service.admin.AdminSubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/subjects")
@RequiredArgsConstructor
public class AdminSubjectController {

    private final AdminSubjectService adminSubjectService;

    @GetMapping
    public ResponseEntity<BaseResponse<AdminPageResult<AdminSubjectItem>>> getSubjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(name = "department_id", required = false) Long departmentId) {
        return ResponseEntity.ok(BaseResponse.success(
                adminSubjectService.getSubjects(page, size, keyword, departmentId)));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<CreatedIdResult>> createSubject(
            @Valid @RequestBody CreateSubjectRequest request) {
        return ResponseEntity.ok(BaseResponse.success("Created",
                adminSubjectService.createSubject(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<Boolean>> updateSubject(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSubjectRequest request) {
        return ResponseEntity.ok(BaseResponse.success("Updated",
                adminSubjectService.updateSubject(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Boolean>> deleteSubject(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponse.success("Deleted",
                adminSubjectService.deleteSubject(id)));
    }
}
