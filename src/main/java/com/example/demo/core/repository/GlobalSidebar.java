package com.example.demo.core.repository;

import com.example.demo.core.model.MetaDataDTO;
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
    private final List<Map<String, Object>> inquirySidebarMenu;

    public GlobalSidebar() {
        sidebarMenu = new ArrayList<>();
        inquirySidebarMenu = new ArrayList<>();
        EntityRegistry.all().forEach((key, clazz) -> {
            try {
                MetaDataDTO metaData = MetadataValidator.extractMetaData(clazz);
                if (metaData.getType().equalsIgnoreCase("inquiry")) {
                    inquirySidebarMenu.add(metaData.toMap());
                } else {
                    sidebarMenu.add(metaData.toMap());
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public List<Map<String, Object>> getSidebarMenu() {
        return sidebarMenu;
    }

    public List<Map<String, Object>> getInquirySidebarMenu() {
        return inquirySidebarMenu;
    }
}
