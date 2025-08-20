package com.example.demo.controller.api;

import com.example.demo.dto.response.ApiResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BaseController {

    protected <T> ResponseEntity<ApiResponseDTO<T>> ok(T data) {
        return ResponseEntity.ok(ApiResponseDTO.success(data));
    }

    protected <T> ResponseEntity<ApiResponseDTO<T>> ok(String data) {
        return ResponseEntity.ok(ApiResponseDTO.success(data));
    }

    protected <T> ResponseEntity<ApiResponseDTO<T>> error(String message) {
        return ResponseEntity.status(500).body(ApiResponseDTO.error(message));
    }

    protected <T> ResponseEntity<ApiResponseDTO<T>> response(HttpStatus status, T data, String message) {
        return ResponseEntity.status(status).body(ApiResponseDTO.of(status, data, message));
    }

    protected <T> ResponseEntity<ApiResponseDTO<T>> response(HttpStatus status, T data) {
        return ResponseEntity.status(status).body(ApiResponseDTO.of(status, data));
    }
}
