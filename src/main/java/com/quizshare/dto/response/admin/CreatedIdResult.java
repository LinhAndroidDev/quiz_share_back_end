package com.quizshare.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreatedIdResult {
    private Long id;
    private String title;
}
