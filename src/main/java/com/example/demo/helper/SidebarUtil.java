package com.example.demo.helper;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component("sidebarUtil")
public class SidebarUtil {

    public boolean isAnyActive(List<Map<String, Object>> sidebarMenu, String requestUri) {
        return sidebarMenu.stream()
                .map(m -> (String) m.get("href"))
                .anyMatch(requestUri::startsWith);
    }
}
