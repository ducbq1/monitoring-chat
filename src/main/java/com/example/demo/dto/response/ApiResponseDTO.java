package com.example.demo.dto.response;

import org.springframework.http.HttpStatusCode;

public class ApiResponseDTO<T> {
    private int code;
    private String message;
    private T data;

    private ApiResponseDTO(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponseDTO<T> of(HttpStatusCode statusCode, T data, String message) {
        return new ApiResponseDTO<>(statusCode.value(), message, data);
    }

    public static <T> ApiResponseDTO<T> of(HttpStatusCode statusCode, T data) {
        return new ApiResponseDTO<>(statusCode.value(), null, data);
    }

    public static <T> ApiResponseDTO<T> success(T data, String message) {
        return new ApiResponseDTO<>(200, message, data);
    }

    public static <T> ApiResponseDTO<T> success(T data) {
        return new ApiResponseDTO<>(200, "success", data);
    }

    public static <T> ApiResponseDTO<T> success(String message) {
        return new ApiResponseDTO<>(200, message, null);
    }

    public static <T> ApiResponseDTO<T> error(String message) {
        return new ApiResponseDTO<>(500, message, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
