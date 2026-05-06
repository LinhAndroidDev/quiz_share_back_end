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

    /** Legacy endpoint — giữ lại để không break Android client cũ */
    @PostMapping("/postUploadFile")
    public ResponseEntity<BaseResponse<String>> postUploadFile(
            @RequestParam("user_id") Long userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("folder_name") String folderName,
            @RequestParam("file_name") String fileName) {
        String url = fileStorageService.uploadFile(file, folderName, fileName);
        return ResponseEntity.ok(BaseResponse.success("Uploaded", url));
    }

    /**
     * Upload ảnh lên storage (Cloudinary hoặc local).
     *
     * Request: multipart/form-data
     *   - file        : file ảnh (jpeg/png/webp)
     *   - folder_name : tên thư mục lưu trữ (vd: "avatars", "exams", "questions")
     *   - file_name   : tên file mong muốn (tuỳ chọn — bỏ trống sẽ sinh UUID)
     *
     * Response: { "data": "https://res.cloudinary.com/..." }
     */
    @PostMapping("/uploadImage")
    public ResponseEntity<BaseResponse<String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("folder_name") String folderName,
            @RequestParam(value = "file_name", required = false, defaultValue = "") String fileName) {
        String url = fileStorageService.uploadFile(file, folderName, fileName);
        return ResponseEntity.ok(BaseResponse.success("Uploaded", url));
    }

}
