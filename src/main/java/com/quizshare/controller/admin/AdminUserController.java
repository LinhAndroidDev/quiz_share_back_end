package com.quizshare.controller.admin;

import com.quizshare.dto.request.admin.UpdateUserRoleRequest;
import com.quizshare.dto.request.admin.UpdateUserStatusRequest;
import com.quizshare.dto.response.BaseResponse;
import com.quizshare.dto.response.admin.AdminPageResult;
import com.quizshare.dto.response.admin.AdminUserDetailResult;
import com.quizshare.dto.response.admin.AdminUserItem;
import com.quizshare.service.admin.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<BaseResponse<AdminPageResult<AdminUserItem>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false, defaultValue = "") String status,
            @RequestParam(required = false, defaultValue = "") String role) {
        return ResponseEntity.ok(BaseResponse.success(
                adminUserService.getUsers(page, size, keyword, status, role)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<AdminUserDetailResult>> getUserDetail(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponse.success(adminUserService.getUserDetail(id)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BaseResponse<Boolean>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        return ResponseEntity.ok(BaseResponse.success("Updated",
                adminUserService.updateStatus(id, request.getStatus())));
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<BaseResponse<Boolean>> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRoleRequest request) {
        return ResponseEntity.ok(BaseResponse.success("Updated",
                adminUserService.updateRole(id, request.getRole())));
    }
}
