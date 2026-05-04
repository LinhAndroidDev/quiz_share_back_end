package com.quizshare.controller;

import com.quizshare.dto.request.*;
import com.quizshare.dto.response.BaseResponse;
import com.quizshare.dto.response.UserResult;
import com.quizshare.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/getUserInfo")
    public ResponseEntity<BaseResponse<UserResult>> getUserInfo(
            @Valid @RequestBody UserIdRequest request) {
        UserResult result = userService.getUserInfo(request.getUserId());
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @PostMapping("/updateUserInfo")
    public ResponseEntity<BaseResponse<String>> updateUserInfo(
            @Valid @RequestBody UpdateUserInfoRequest request) {
        String result = userService.updateUserInfo(request);
        return ResponseEntity.ok(BaseResponse.success("Updated", result));
    }

    @PostMapping("/changeEmail")
    public ResponseEntity<BaseResponse<String>> changeEmail(
            @Valid @RequestBody ChangeEmailRequest request) {
        String result = userService.changeEmail(request);
        return ResponseEntity.ok(BaseResponse.success("Updated", result));
    }

    @PostMapping("/changePassword")
    public ResponseEntity<BaseResponse<Boolean>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {
        boolean result = userService.changePassword(request);
        return ResponseEntity.ok(BaseResponse.success("Updated", result));
    }

    @PostMapping("/editAvatar")
    public ResponseEntity<BaseResponse<Boolean>> editAvatar(
            @RequestParam("user_id") Long userId,
            @RequestParam("file") MultipartFile file) {
        boolean result = userService.editAvatar(userId, file);
        return ResponseEntity.ok(BaseResponse.success("Updated", result));
    }

    @PostMapping("/unpublicUser")
    public ResponseEntity<BaseResponse<String>> unpublicUser(
            @Valid @RequestBody UserIdRequest request) {
        String result = userService.unpublicUser(request.getUserId());
        return ResponseEntity.ok(BaseResponse.success("User deactivated", result));
    }
}
