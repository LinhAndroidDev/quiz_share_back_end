package com.quizshare.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {

    private int statusCode;
    private String message;
    private T result;

    public static <T> BaseResponse<T> success(T result) {
        return BaseResponse.<T>builder()
                .statusCode(200)
                .message("Success")
                .result(result)
                .build();
    }

    public static <T> BaseResponse<T> success(String message, T result) {
        return BaseResponse.<T>builder()
                .statusCode(200)
                .message(message)
                .result(result)
                .build();
    }

    public static <T> BaseResponse<T> error(int statusCode, Object message) {
        return BaseResponse.<T>builder()
                .statusCode(statusCode)
                .message(message != null ? message.toString() : "Error")
                .build();
    }
}
