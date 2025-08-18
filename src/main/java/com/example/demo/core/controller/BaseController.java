package com.example.demo.core.controller;

import com.example.demo.core.entity.BaseEntity;
import com.example.demo.core.entity.GlobalDefault;
import com.example.demo.core.model.MetaDataDTO;
import com.example.demo.core.model.TablePageDTO;
import com.example.demo.core.repository.RepositoryFactory;
import com.example.demo.core.service.GenericService;
import com.example.demo.core.service.JdbcService;
import com.example.demo.core.service.ServiceFactory;
import com.example.demo.helper.ViewHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

public abstract class BaseController<T extends BaseEntity> {
    private final GenericService genericService;
    private final Class<T> entityClass;
    private final JdbcService<T> jdbcService;

    protected final String basePath;
    protected final String viewPrefix;

    protected BaseController(ServiceFactory serviceFactory, GenericService genericService, Class<T> entityClass, String basePath, String viewPrefix) {
        this.genericService = genericService;
        this.entityClass = entityClass;
        this.basePath = basePath;
        this.viewPrefix = viewPrefix;
        jdbcService = serviceFactory.getService(entityClass);
    }

    @GetMapping
    public String list(Model model,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "5") int size) {
        long totalElements = jdbcService.count();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        List<T> records = jdbcService.paginate(page, size);
        List<Map<String, Object>> columns = genericService.getColumns(entityClass);
        MetaDataDTO metadata = genericService.getMetadata(entityClass);

        TablePageDTO<T> dto = new TablePageDTO<T>()
                .setMetadata(metadata)
                .setColumns(genericService.getColumns(entityClass))
                .setRecords(records)
                .setNumber(page)
                .setSize(size)
                .setTotalPages(totalPages)
                .setTotalElements(totalElements)
                .setNumberOfElements(Math.min(size, (int)(totalElements - page * size)))
                .setFirst(page == 0)
                .setLast(page >= totalPages - 1);

        model.addAttribute("table", dto);
        
        ViewHelper.setView(model, viewPrefix + "/list", metadata.getTitle());
        return "layout";
    }

    @GetMapping("/create")
    public String createForm(Model model, HttpServletRequest request) {
        MetaDataDTO metadata = genericService.getMetadata(entityClass);
        List<Map<String, Object>> columns = genericService.getColumns(entityClass);
        T record = genericService.create(entityClass);
        model.addAttribute("columns", columns);
        model.addAttribute("record", record);
        model.addAttribute("listUri", basePath);
        model.addAttribute("requestUri", request.getRequestURI());
        ViewHelper.setView(model, viewPrefix + "/form", metadata.getTitle());
        return "layout";
    }

    @PostMapping("/create")
    public String createSubmit(@ModelAttribute T record) {
        jdbcService.insert(record);
        return "redirect:" + basePath;
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Object id, Model model, HttpServletRequest request) {
        MetaDataDTO metadata = genericService.getMetadata(entityClass);
        T record = jdbcService.findById(id);
        model.addAttribute("record", record);
        model.addAttribute("columns", genericService.getColumns(entityClass));
        model.addAttribute("listUri", basePath);
        model.addAttribute("requestUri", request.getRequestURI());
        ViewHelper.setView(model, "generic/form", metadata.getTitle());
        return "layout";
    }

    @PostMapping("/edit/{id}")
    public String editSubmit(@PathVariable Object id, @ModelAttribute T record) {
        genericService.setPrimaryKeyValue(record, id);
        jdbcService.update(record);
        return "redirect:" + basePath;
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Object id) {
        jdbcService.deleteById(id);
        return "redirect:" + basePath;
    }
}
