package com.quizshare.controller.admin;

import com.quizshare.dto.request.admin.CreateDepartmentRequest;
import com.quizshare.dto.request.admin.UpdateDepartmentRequest;
import com.quizshare.dto.response.BaseResponse;
import com.quizshare.dto.response.admin.AdminDepartmentItem;
import com.quizshare.dto.response.admin.AdminPageResult;
import com.quizshare.dto.response.admin.CreatedIdResult;
import com.quizshare.service.admin.AdminDepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/departments")
@RequiredArgsConstructor
public class AdminDepartmentController {

    private final AdminDepartmentService adminDepartmentService;

    @GetMapping
    public ResponseEntity<BaseResponse<AdminPageResult<AdminDepartmentItem>>> getDepartments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "") String keyword) {
        return ResponseEntity.ok(BaseResponse.success(
                adminDepartmentService.getDepartments(page, size, keyword)));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<CreatedIdResult>> createDepartment(
            @Valid @RequestBody CreateDepartmentRequest request) {
        return ResponseEntity.ok(BaseResponse.success("Created",
                adminDepartmentService.createDepartment(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<Boolean>> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDepartmentRequest request) {
        return ResponseEntity.ok(BaseResponse.success("Updated",
                adminDepartmentService.updateDepartment(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Boolean>> deleteDepartment(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponse.success("Deleted",
                adminDepartmentService.deleteDepartment(id)));
    }
}
