package com.example.demo.core.controller;

import com.example.demo.core.entity.BaseEntity;
import com.example.demo.core.model.ColumnData;
import com.example.demo.core.model.MetaDataDTO;
import com.example.demo.core.repository.EntityRegistry;
import com.example.demo.core.service.JdbcService;
import com.example.demo.core.service.ServiceFactory;
import com.example.demo.helper.FieldUtil;
import com.example.demo.helper.ViewHelper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@Controller
@RequestMapping("/admin/generic/inquiry")
public class EntityLookupController {

    private final JdbcTemplate jdbcTemplate;
    private final ServiceFactory serviceFactory;
    private final FieldUtil fieldUtil;

    public EntityLookupController(JdbcTemplate jdbcTemplate, ServiceFactory serviceFactory, FieldUtil fieldUtil) {
        this.jdbcTemplate = jdbcTemplate;
        this.serviceFactory = serviceFactory;
        this.fieldUtil = fieldUtil;
    }

    @GetMapping("/{entity}")
    public String showLookupForm(Model model, @PathVariable String entity) throws SQLException {
        Class<? extends BaseEntity> clazz = EntityRegistry.get(entity);
        if (clazz == null) return "redirect:/admin";
        MetaDataDTO metadata = fieldUtil.getMetadata(clazz);

        JdbcService jdbcService = serviceFactory.getService(clazz);

        model.addAttribute("metadata", jdbcService.getDatabaseInfo());
        model.addAttribute("entity", entity);
        model.addAttribute("primaryKeyLabel", jdbcService.getPrimaryKeyLabel(entity));
        ViewHelper.setView(model, "generic/entity-lookup", metadata.getTitle());
        return "layout";
    }

    @PostMapping("{entity}")
    public String lookupRecord(@PathVariable String entity, @RequestParam("primaryKeyValue") String primaryKeyValue, Model model) throws SQLException {
        Class<? extends BaseEntity> clazz = EntityRegistry.get(entity);
        if (clazz == null) return "redirect:/admin";
        MetaDataDTO metadata = fieldUtil.getMetadata(clazz);

        JdbcService jdbcService = serviceFactory.getService(clazz);

        List<ColumnData> record = jdbcService.getRecordWithMetadata(entity, primaryKeyValue);
        model.addAttribute("metadata", jdbcService.getDatabaseInfo());
        model.addAttribute("entity", entity);
        model.addAttribute("columns", record);
        model.addAttribute("primaryKeyLabel", jdbcService.getPrimaryKeyLabel(entity));
        model.addAttribute("primaryKeyValue", primaryKeyValue);
        ViewHelper.setView(model, "generic/entity-lookup", metadata.getTitle());
        return "layout";    }
}
