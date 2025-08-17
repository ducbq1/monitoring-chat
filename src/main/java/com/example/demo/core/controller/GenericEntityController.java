package com.example.demo.core.controller;

import com.example.demo.core.entity.BaseEntity;
import com.example.demo.core.entity.GlobalDefault;
import com.example.demo.core.model.TablePageDTO;
import com.example.demo.core.repository.EntityRegistry;
import com.example.demo.core.repository.RepositoryFactory;
import com.example.demo.core.service.GenericService;
import com.example.demo.core.service.JdbcService;
import com.example.demo.core.service.ServiceFactory;
import com.example.demo.helper.FieldUtil;
import com.example.demo.helper.ViewHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/generic")
public class GenericEntityController {
    private final ServiceFactory serviceFactory;
    private final GenericService genericService;
    private final FieldUtil fieldUtil;

    protected GenericEntityController(ServiceFactory serviceFactory, GenericService genericService, FieldUtil fieldUtil) {
        this.serviceFactory = serviceFactory;
        this.genericService = genericService;
        this.fieldUtil = fieldUtil;
    }

    @GetMapping("/{entity}")
    public String list(Model model,
                       @PathVariable String entity,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "5") int size) {
        Class<? extends BaseEntity> clazz = EntityRegistry.get(entity);
        if (clazz == null) return "redirect:/admin";

        JdbcService jdbcService = serviceFactory.getService(clazz);
        long totalElements = jdbcService.count();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        List<BaseEntity> records = jdbcService.paginate(page, size);
        List<Map<String, String>> columns = genericService.getColumns(clazz);

        TablePageDTO<BaseEntity> dto = new TablePageDTO<BaseEntity>()
                .setTableName(genericService.getTableTitle(clazz))
                .setColumns(columns)
                .setRecords(records)
                .setNumber(page)
                .setSize(size)
                .setTotalPages(totalPages)
                .setTotalElements(totalElements)
                .setNumberOfElements(records.size())
                .setFirst(page == 0)
                .setLast(page >= totalPages - 1);

        model.addAttribute("table", dto);
        ViewHelper.setView(model, "generic/list", dto.getTableName());
        return "layout";
    }

    @GetMapping({"/{entity}/create", "/{entity}/edit/{id}"})
    public String form(@PathVariable String entity,
                       @PathVariable(required = false) Object id,
                       Model model,
                       HttpServletRequest request) {
        Class<? extends BaseEntity> clazz = EntityRegistry.get(entity);
        if (clazz == null) return "redirect:/admin";

        JdbcService jdbcService = serviceFactory.getService(clazz);
        BaseEntity record = (id != null) ? jdbcService.findById(id) : genericService.create(clazz);

        String listUri = UriComponentsBuilder.fromPath("/admin/generic/{entity}")
                .buildAndExpand(entity)
                .toUriString();
        model.addAttribute("listUri", listUri);
        model.addAttribute("record", record);
        model.addAttribute("columns", genericService.getColumns(clazz));
        model.addAttribute("requestUri", request.getRequestURI());
        ViewHelper.setView(model, "generic/form", genericService.getTableTitle(clazz));
        return "layout";
    }

    @PostMapping({"/{entity}/create", "/{entity}/edit/{id}"})
    public String submit(@PathVariable String entity,
                         @PathVariable(required = false) Object id,
                         HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        Class<? extends BaseEntity> clazz = EntityRegistry.get(entity);
        if (clazz == null) return "redirect:/admin";

        BaseEntity record = genericService.create(clazz);

        ConvertUtils.register(new Converter() {
            @Override
            public <T> T convert(Class<T> type, Object value) {
                if (value == null) return null;
                if (type == LocalDateTime.class) {
                    return type.cast(LocalDateTime.parse(value.toString()));
                }
                return type.cast(value);
            }
        }, LocalDateTime.class);

        BeanUtils.populate(record, request.getParameterMap());

        JdbcService jdbcService = serviceFactory.getService(clazz);

        if (id != null) {
            fieldUtil.setPrimaryKeyValue(record, id);
            jdbcService.update(record);
        } else {
            jdbcService.insert(record);
        }
        return "redirect:/admin/generic/" + entity;
    }

    @PostMapping("/{entity}/delete/{id}")
    public String delete(@PathVariable String entity, @PathVariable Object id) {
        Class<? extends BaseEntity> clazz = EntityRegistry.get(entity);
        if (clazz == null) return "redirect:/admin";

        JdbcService jdbcService = serviceFactory.getService(clazz);
        jdbcService.deleteById(id);
        return "redirect:/admin/generic/" + entity;
    }
}
