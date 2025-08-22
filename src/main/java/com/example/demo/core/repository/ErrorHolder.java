package com.example.demo.core.repository;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class ErrorHolder {
    private String title;
    private String message;

    

    public void setError(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public boolean hasError() {
        return title != null;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}
