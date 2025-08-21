package com.example.demo.core.controller;

import com.example.demo.core.entity.BaseEntity;
import com.example.demo.core.model.*;
import com.example.demo.core.repository.EntityRegistry;
import com.example.demo.core.repository.ErrorHolder;
import com.example.demo.core.service.JdbcService;
import com.example.demo.core.service.ServiceFactory;
import com.example.demo.helper.FieldUtil;
import com.example.demo.helper.ViewHelper;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/generic/inquiry")
public class EntityLookupController extends EntityLookupBaseController {

    private final JdbcTemplate jdbcTemplate;
    private final ServiceFactory serviceFactory;
    private final FieldUtil fieldUtil;

    public EntityLookupController(ErrorHolder errorHolder, MessageSource messageSource,
                                  JdbcTemplate jdbcTemplate, ServiceFactory serviceFactory,
                                  FieldUtil fieldUtil) {
        super(errorHolder, messageSource);
        this.jdbcTemplate = jdbcTemplate;
        this.serviceFactory = serviceFactory;
        this.fieldUtil = fieldUtil;
    }

    @GetMapping("/{entity}")
    public String showLookupForm(Model model, @PathVariable String entity) throws SQLException {
        return buildLookupPage(model, entity, null);
    }

    @PostMapping("/{entity}")
    public String lookupRecord(@PathVariable String entity,
                               @RequestParam("primaryKeyValue") String primaryKeyValue,
                               Model model) throws SQLException {
        return buildLookupPage(model, entity, primaryKeyValue);
    }

    private String buildLookupPage(Model model, String entity, String primaryKeyValue) throws SQLException {
        Class<? extends BaseEntity> clazz = EntityRegistry.get(entity);
        if (clazz == null) return "redirect:/admin";

        MetaDataDTO metadata = fieldUtil.getMetadata(clazz);
        JdbcService jdbcService = serviceFactory.getService(clazz);

        DatabaseDTO database = jdbcService.getDatabaseInfo(entity);
        metadata.setDatabase(database);

        TablePageDTO<ColumnDataDTO> table = new TablePageDTO<ColumnDataDTO>().setMetadata(metadata);

        List<ColumnDataDTO> records = jdbcService.getRecordWithMetadata(entity, database.getPrimaryKey(), primaryKeyValue);
        List<Map<String, Object>> columns = fieldUtil.getColumns(ColumnDataDTO.class);
        table.setColumns(columns).setRecords(records);

        model.addAttribute("primaryKeyValue", primaryKeyValue);
        model.addAttribute("table", table);
        return render(model, "generic/entity-lookup", metadata.getTitle());
    }
}
