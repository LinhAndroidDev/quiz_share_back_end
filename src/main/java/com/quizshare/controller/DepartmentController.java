package com.quizshare.controller;

import com.quizshare.dto.request.GetDepartmentListRequest;
import com.quizshare.dto.request.UserIdRequest;
import com.quizshare.dto.response.BaseResponse;
import com.quizshare.dto.response.DepartmentInfoResult;
import com.quizshare.dto.response.DepartmentResult;
import com.quizshare.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping("/getDepartmentList")
    public ResponseEntity<BaseResponse<List<DepartmentResult>>> getDepartmentList(
            @Valid @RequestBody GetDepartmentListRequest request) {
        List<DepartmentResult> result = departmentService.getDepartmentList(
                request.getUserId(), request.getKeyword());
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @PostMapping("/listDepartmentInfo")
    public ResponseEntity<BaseResponse<List<DepartmentInfoResult>>> listDepartmentInfo(
            @Valid @RequestBody UserIdRequest request) {
        List<DepartmentInfoResult> result = departmentService.listDepartmentInfo();
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
