package com.quizshare.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResult {

    @JsonProperty("access_token")
    private String accessToken;

    private Long id;
    private String name;
    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    private String birthday;
    private String avatar;
    private String role;
    private String status;
}
