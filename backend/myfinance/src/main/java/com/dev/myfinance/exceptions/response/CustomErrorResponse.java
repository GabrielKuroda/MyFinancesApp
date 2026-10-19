package com.dev.myfinance.exceptions.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CustomErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> details
) {
    public static CustomErrorResponse of(int status, String error, String message, String path) {
        return new CustomErrorResponse(Instant.now(), status, error, message, path, null);
    }

    public static CustomErrorResponse of(int status, String error, String message, String path, Map<String, String> details) {
        return new CustomErrorResponse(Instant.now(), status, error, message, path, details);
    }
}