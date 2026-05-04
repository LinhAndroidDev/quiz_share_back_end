package com.quizshare.controller;

import com.quizshare.dto.request.SearchSubjectRequest;
import com.quizshare.dto.response.BaseResponse;
import com.quizshare.dto.response.SubjectResult;
import com.quizshare.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping("/searchSubject")
    public ResponseEntity<BaseResponse<List<SubjectResult>>> searchSubject(
            @Valid @RequestBody SearchSubjectRequest request) {
        List<SubjectResult> result = subjectService.searchSubjects(
                request.getDepartmentId(), request.getKeyword());
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
