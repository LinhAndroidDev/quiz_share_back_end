package com.quizshare.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
@ConfigurationProperties(prefix = "app.storage")
@Data
@Slf4j
public class StorageConfig {

    private String type;
    private GcsProperties gcs = new GcsProperties();
    private LocalProperties local = new LocalProperties();

    @Data
    public static class GcsProperties {
        private String bucketName;
        private String projectId;
        private String credentialsPath;
    }

    @Data
    public static class LocalProperties {
        private String uploadDir;
        private String baseUrl;
    }

    @Bean
    @ConditionalOnProperty(name = "app.storage.type", havingValue = "gcs")
    public Storage googleCloudStorage() throws IOException {
        try {
            GoogleCredentials credentials = GoogleCredentials
                    .fromStream(new FileInputStream(gcs.getCredentialsPath()));
            return StorageOptions.newBuilder()
                    .setProjectId(gcs.getProjectId())
                    .setCredentials(credentials)
                    .build()
                    .getService();
        } catch (IOException e) {
            log.error("Failed to initialize Google Cloud Storage: {}", e.getMessage());
            throw e;
        }
    }
}
