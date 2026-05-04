package com.quizshare.controller;

import com.quizshare.dto.request.ForgotPasswordRequest;
import com.quizshare.dto.request.LoginRequest;
import com.quizshare.dto.request.RegisterRequest;
import com.quizshare.dto.response.BaseResponse;
import com.quizshare.dto.response.LoginResult;
import com.quizshare.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<LoginResult>> register(
            @Valid @RequestBody RegisterRequest request) {
        LoginResult result = authService.register(request);
        return ResponseEntity.ok(BaseResponse.success("Register successful", result));
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResult>> login(
            @Valid @RequestBody LoginRequest request) {
        LoginResult result = authService.login(request);
        return ResponseEntity.ok(BaseResponse.success("Login successful", result));
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<BaseResponse<Boolean>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        boolean result = authService.forgotPassword(request);
        return ResponseEntity.ok(BaseResponse.success("Email sent", result));
    }
}
