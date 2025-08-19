package com.example.demo.core.repository;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class ViewContext {
    private String currentView;

    public String getCurrentView() {
        return currentView;
    }

    public void setCurrentView(String viewName) {
        this.currentView = viewName;
    }
}
