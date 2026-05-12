package com.quizshare.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Success
    SUCCESS(200, "Success", HttpStatus.OK),

    // Client errors
    INVALID_REQUEST(400, "Invalid request data", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(401, "Unauthorized - invalid or expired token", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(403, "Forbidden - insufficient permissions", HttpStatus.FORBIDDEN),
    NOT_FOUND(404, "Resource not found", HttpStatus.NOT_FOUND),
    CONFLICT(409, "Conflict - resource already exists", HttpStatus.CONFLICT),

    // Domain-specific errors
    USER_NOT_FOUND(404, "User not found", HttpStatus.NOT_FOUND),
    EMAIL_ALREADY_EXISTS(409, "Email already exists", HttpStatus.CONFLICT),
    PHONE_ALREADY_EXISTS(409, "Phone number already exists", HttpStatus.CONFLICT),
    INVALID_CREDENTIALS(401, "Invalid email/phone or password", HttpStatus.UNAUTHORIZED),
    USER_BANNED(403, "Account has been banned", HttpStatus.FORBIDDEN),
    USER_INACTIVE(403, "Account is inactive", HttpStatus.FORBIDDEN),
    PASSWORD_MISMATCH(400, "Passwords do not match", HttpStatus.BAD_REQUEST),

    DEPARTMENT_NOT_FOUND(404, "Department not found", HttpStatus.NOT_FOUND),
    DEPARTMENT_HAS_SUBJECTS(400, "Cannot delete: department still has subjects", HttpStatus.BAD_REQUEST),
    SUBJECT_NOT_FOUND(404, "Subject not found", HttpStatus.NOT_FOUND),
    SUBJECT_HAS_EXAMS(400, "Cannot delete: subject still has exams", HttpStatus.BAD_REQUEST),
    EXAM_NOT_FOUND(404, "Exam not found", HttpStatus.NOT_FOUND),
    EXAM_HISTORY_NOT_FOUND(404, "Exam history not found", HttpStatus.NOT_FOUND),
    QUESTION_NOT_FOUND(404, "Question not found", HttpStatus.NOT_FOUND),
    ANSWER_NOT_FOUND(404, "Answer not found", HttpStatus.NOT_FOUND),

    FILE_UPLOAD_FAILED(500, "File upload failed", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_FILE_TYPE(400, "Invalid file type", HttpStatus.BAD_REQUEST),

    // Server error
    INTERNAL_ERROR(500, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int statusCode;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int statusCode, String message, HttpStatus httpStatus) {
        this.statusCode = statusCode;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
