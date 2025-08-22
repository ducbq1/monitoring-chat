package com.example.demo.core.controller;

import com.example.demo.core.entity.BaseEntity;
import com.example.demo.core.model.DatabaseDTO;
import com.example.demo.core.model.MessageDTO;
import com.example.demo.core.model.MetaDataDTO;
import com.example.demo.core.model.TablePageDTO;
import com.example.demo.core.repository.EntityRegistry;
import com.example.demo.core.repository.ViewContext;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/generic/list")
public class GenericEntityController {
    private final ViewContext viewContext;
    private final ServiceFactory serviceFactory;
    private final FieldUtil fieldUtil;

    protected GenericEntityController(ViewContext viewContext, ServiceFactory serviceFactory, FieldUtil fieldUtil) {
        this.viewContext = viewContext;
        this.serviceFactory = serviceFactory;
        this.fieldUtil = fieldUtil;
    }

    @GetMapping("/{entity}")
    public String list(Model model,
                       @PathVariable String entity,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "5") int size,
                       HttpServletRequest request) {
        viewContext.setCurrentView(request.getRequestURI());
        Class<? extends BaseEntity> clazz = EntityRegistry.get(entity);
        if (clazz == null) return "redirect:/admin";
        MetaDataDTO metadata = fieldUtil.getMetadata(clazz);

        Map<String, String[]> rawParams = request.getParameterMap();
        Map<String, String> filters = new HashMap<>();

        rawParams.forEach((key, value) -> {
            if (!"page".equals(key) && !"size".equals(key) && value != null && value.length > 0 && !value[0].isBlank()) {
                filters.put(key, value[0].trim());
            }
        });

        JdbcService jdbcService = serviceFactory.getService(clazz);
        long totalElements = filters.isEmpty() ? jdbcService.count() : jdbcService.count(filters);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        List<BaseEntity> records = filters.isEmpty() ? jdbcService.paginate(page, size) :  jdbcService.paginate(page, size, filters);
        List<Map<String, Object>> columns = fieldUtil.getColumns(clazz);

        DatabaseDTO database = jdbcService.getDatabaseInfo();
        metadata.setDatabase(database);

        TablePageDTO<BaseEntity> dto = new TablePageDTO<BaseEntity>()
                .setMetadata(metadata)
                .setColumns(columns)
                .setRecords(records)
                .setNumber(page)
                .setSize(size)
                .setTotalPages(totalPages)
                .setTotalElements(totalElements)
                .setNumberOfElements(records.size())
                .setFirst(page == 0)
                .setLast(page >= totalPages - 1);


        int start = Math.max(0, page - 3);
        int end = Math.min(totalPages - 1, page + 3);

        model.addAttribute("params", filters);
        model.addAttribute("pageStart", start);
        model.addAttribute("pageEnd", end);
        model.addAttribute("table", dto);
        ViewHelper.setView(model, "generic/list", metadata.getTitle());
        return "layout";
    }

    @GetMapping({"/{entity}/create", "/{entity}/edit/{id}"})
    public String form(@PathVariable String entity,
                       @PathVariable(required = false) Object id,
                       Model model,
                       HttpServletRequest request) {
        viewContext.setCurrentView(request.getRequestURI());
        Class<? extends BaseEntity> clazz = EntityRegistry.get(entity);
        if (clazz == null) return "redirect:/admin";
        MetaDataDTO metadata = fieldUtil.getMetadata(clazz);

        JdbcService jdbcService = serviceFactory.getService(clazz);
        BaseEntity record = (id != null) ? jdbcService.findById(id) : fieldUtil.create(clazz);

        String listUri = UriComponentsBuilder.fromPath("/admin/generic/list/{entity}")
                .buildAndExpand(entity)
                .toUriString();
        model.addAttribute("listUri", listUri);
        model.addAttribute("record", record);
        model.addAttribute("columns", fieldUtil.getColumns(clazz));
        model.addAttribute("requestUri", request.getRequestURI());
        ViewHelper.setView(model, "generic/form", metadata.getTitle());
        return "layout";
    }

    @PostMapping({"/{entity}/create", "/{entity}/edit/{id}"})
    public String submit(@PathVariable String entity,
                         @PathVariable(required = false) Object id,
                         RedirectAttributes redirectAttributes,
                         HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        viewContext.setCurrentView(request.getRequestURI());
        Class<? extends BaseEntity> clazz = EntityRegistry.get(entity);
        if (clazz == null) return "redirect:/admin";

        BaseEntity record = fieldUtil.create(clazz);

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

        ConvertUtils.register(new Converter() {
            @Override
            public Object convert(Class type, Object value) {
                if (value == null || "".equals(value)) return null;
                return new BigDecimal(value.toString());
            }
        }, BigDecimal.class);

        BeanUtils.populate(record, request.getParameterMap());

        JdbcService jdbcService = serviceFactory.getService(clazz);

        if (id != null) {
            fieldUtil.setPrimaryKeyValue(record, id);
            jdbcService.update(record);
        } else {
            jdbcService.insert(record);
        }

        redirectAttributes.addFlashAttribute("message", MessageDTO.success("Thông báo", "Thao tác thành công"));
        return "redirect:/admin/generic/list/" + entity;
    }

    @PostMapping("/{entity}/delete/{id}")
    public String delete(@PathVariable String entity, @PathVariable Object id, RedirectAttributes redirectAttributes) {
        Class<? extends BaseEntity> clazz = EntityRegistry.get(entity);
        if (clazz == null) return "redirect:/admin";

        JdbcService jdbcService = serviceFactory.getService(clazz);
        jdbcService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", MessageDTO.success("Thông báo", "Thao tác thành công"));
        return "redirect:/admin/generic/list/" + entity;
    }
}
