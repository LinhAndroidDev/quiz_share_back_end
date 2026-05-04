package com.quizshare.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.quizshare.config.StorageConfig;
import com.quizshare.exception.AppException;
import com.quizshare.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    private final StorageConfig storageConfig;

    @Nullable
    private final Storage googleStorage;

    @Autowired
    public FileStorageService(StorageConfig storageConfig,
                               @Nullable Storage googleStorage) {
        this.storageConfig = storageConfig;
        this.googleStorage = googleStorage;
    }

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp"
    );

    public String uploadFile(MultipartFile file, String folderName, String fileName) {
        validateFile(file);

        String storageType = storageConfig.getType();
        if ("gcs".equalsIgnoreCase(storageType) && googleStorage != null) {
            return uploadToGcs(file, folderName, fileName);
        } else {
            return uploadToLocal(file, folderName, fileName);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, "File is empty");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE,
                    "Only JPEG, PNG, WEBP images are allowed");
        }
    }

    private String uploadToGcs(MultipartFile file, String folderName, String fileName) {
        try {
            String objectName = buildObjectName(folderName, fileName, file);
            BlobId blobId = BlobId.of(storageConfig.getGcs().getBucketName(), objectName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();
            googleStorage.create(blobInfo, file.getBytes());
            return String.format("https://storage.googleapis.com/%s/%s",
                    storageConfig.getGcs().getBucketName(), objectName);
        } catch (IOException e) {
            log.error("GCS upload failed: {}", e.getMessage());
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    private String uploadToLocal(MultipartFile file, String folderName, String fileName) {
        try {
            String uploadDir = storageConfig.getLocal().getUploadDir() + folderName + "/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String objectName = buildObjectName(folderName, fileName, file);
            String localFileName = objectName.replace(folderName + "/", "");
            Path targetPath = uploadPath.resolve(localFileName);
            Files.write(targetPath, file.getBytes());
            return storageConfig.getLocal().getBaseUrl() + objectName;
        } catch (IOException e) {
            log.error("Local upload failed: {}", e.getMessage());
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    private String buildObjectName(String folderName, String fileName, MultipartFile file) {
        String extension = getExtension(file.getOriginalFilename());
        String name = (fileName != null && !fileName.isBlank())
                ? fileName.replaceAll("[^a-zA-Z0-9_\\-]", "_")
                : UUID.randomUUID().toString();
        return folderName + "/" + name + extension;
    }

    private String getExtension(String originalFilename) {
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        return ".jpg";
    }
}
