package com.example.demo.core.repository;

import com.example.demo.helper.MetadataValidator;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GlobalSidebar {

    private final List<Map<String, Object>> sidebarMenu;

    public GlobalSidebar() {
        sidebarMenu = new ArrayList<>();
        EntityRegistry.all().forEach((key, clazz) -> {
            try {
                sidebarMenu.add(MetadataValidator.extractMetaData(clazz).toMap());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public List<Map<String, Object>> getSidebarMenu() {
        return sidebarMenu;
    }
}
