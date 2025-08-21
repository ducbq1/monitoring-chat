package com.example.demo.core.listener;

import org.springframework.context.ApplicationEvent;

public class ErrorEvent extends ApplicationEvent {
    private final String title;
    private final String message;

    public ErrorEvent(Object source, String title, String message) {
        super(source);
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}
