package com.example.demo.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handle404() {
        return "error/404";
    }

    @ExceptionHandler(RuntimeException.class)
    public String handle(Exception ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/500";
    }

}
