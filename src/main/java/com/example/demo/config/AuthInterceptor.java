package com.example.demo.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        if (isPublicPath(uri)) return true;

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            if ("GET".equalsIgnoreCase(request.getMethod())) {
                String qs = request.getQueryString();
                String full = uri + (qs != null ? ("?" + qs) : "");
                String encoded = URLEncoder.encode(full, StandardCharsets.UTF_8);
                response.sendRedirect("/login?redirect=" + encoded);
            } else {
                response.sendRedirect("/login?redirect=" + URLEncoder.encode("/dashboard", StandardCharsets.UTF_8));
            }
        }
        return true;
    }

    private boolean isPublicPath(String uri) {
        return uri.startsWith("/login")
                || uri.startsWith("/css")
                || uri.startsWith("/js")
                || uri.startsWith("/images")
                || uri.startsWith("/webjars")
                || uri.equals("/")
                ;
    }
}
