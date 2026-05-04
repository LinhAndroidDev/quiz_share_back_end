package com.quizshare.controller;

import com.quizshare.dto.request.SaveExamRequest;
import com.quizshare.dto.request.SavedExamListRequest;
import com.quizshare.dto.request.SavedSubjectRequest;
import com.quizshare.dto.request.UserIdRequest;
import com.quizshare.dto.response.*;
import com.quizshare.service.SavedService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SavedController {

    private final SavedService savedService;

    @PostMapping("/postSaveExam")
    public ResponseEntity<BaseResponse<SaveExamResult>> postSaveExam(
            @Valid @RequestBody SaveExamRequest request) {
        SaveExamResult result = savedService.toggleSaveExam(request.getUserId(), request.getExamId());
        return ResponseEntity.ok(BaseResponse.success("Saved", result));
    }

    @PostMapping("/savedDepartment")
    public ResponseEntity<BaseResponse<List<DepartmentResult>>> savedDepartment(
            @Valid @RequestBody UserIdRequest request) {
        List<DepartmentResult> result = savedService.getSavedDepartments(request.getUserId());
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @PostMapping("/savedSubject")
    public ResponseEntity<BaseResponse<List<SavedSubjectItem>>> savedSubject(
            @Valid @RequestBody SavedSubjectRequest request) {
        List<SavedSubjectItem> result = savedService.getSavedSubjects(
                request.getUserId(), request.getDepartmentId());
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @PostMapping("/savedExam")
    public ResponseEntity<BaseResponse<SavedExamListResult>> savedExam(
            @Valid @RequestBody SavedExamListRequest request) {
        SavedExamListResult result = savedService.getSavedExams(
                request.getUserId(), request.getSubjectId(), request.getType());
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
