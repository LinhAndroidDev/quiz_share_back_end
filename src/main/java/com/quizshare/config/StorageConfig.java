package com.quizshare.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConfigurationProperties(prefix = "app.storage")
@Data
@Slf4j
public class StorageConfig {

    private String type;
    private CloudinaryProperties cloudinary = new CloudinaryProperties();
    private LocalProperties local = new LocalProperties();

    @Data
    public static class CloudinaryProperties {
        private String cloudName;
        private String uploadPreset;
    }

    @Data
    public static class LocalProperties {
        private String uploadDir;
        private String baseUrl;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
