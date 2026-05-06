package com.quizshare.service;

import com.quizshare.config.StorageConfig;
import com.quizshare.exception.AppException;
import com.quizshare.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    private final StorageConfig storageConfig;
    private final RestTemplate restTemplate;

    @Autowired
    public FileStorageService(StorageConfig storageConfig, RestTemplate restTemplate) {
        this.storageConfig = storageConfig;
        this.restTemplate = restTemplate;
    }

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp"
    );

    public String uploadFile(MultipartFile file, String folderName, String fileName) {
        validateFile(file);

        String storageType = storageConfig.getType();
        return switch (storageType.toLowerCase()) {
            case "cloudinary" -> uploadToCloudinary(file, folderName);
            default -> uploadToLocal(file, folderName, fileName);
        };
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

    @SuppressWarnings("unchecked")
    private String uploadToCloudinary(MultipartFile file, String folderName) {
        try {
            StorageConfig.CloudinaryProperties config = storageConfig.getCloudinary();
            String uploadUrl = "https://api.cloudinary.com/v1_1/" + config.getCloudName() + "/upload";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            byte[] bytes = file.getBytes();
            ByteArrayResource fileResource = new ByteArrayResource(bytes) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename() != null
                            ? file.getOriginalFilename()
                            : UUID.randomUUID() + ".jpg";
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);
            body.add("upload_preset", config.getUploadPreset());
            body.add("folder", folderName);

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.exchange(uploadUrl, HttpMethod.POST, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String secureUrl = (String) response.getBody().get("secure_url");
                if (secureUrl != null) return secureUrl;
            }
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, "Cloudinary did not return a URL");
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Cloudinary upload failed: {}", e.getMessage());
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
