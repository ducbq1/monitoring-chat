package com.example.demo.core.listener;

import com.example.demo.core.repository.ErrorHolder;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ErrorEventListener {
    private final ErrorHolder errorHolder;

    public ErrorEventListener(ErrorHolder errorHolder) {
        this.errorHolder = errorHolder;
    }

    @EventListener
    public void handleErrorEvent(ErrorEvent event) {
        errorHolder.setError(event.getTitle(), event.getMessage());
    }
}
