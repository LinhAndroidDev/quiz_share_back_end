package com.quizshare.controller;

import com.quizshare.dto.response.BaseResponse;
import com.quizshare.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping("/postUploadFile")
    public ResponseEntity<BaseResponse<String>> uploadFile(
            @RequestParam("user_id") Long userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("folder_name") String folderName,
            @RequestParam("file_name") String fileName) {
        String url = fileStorageService.uploadFile(file, folderName, fileName);
        return ResponseEntity.ok(BaseResponse.success("Uploaded", url));
    }
}
