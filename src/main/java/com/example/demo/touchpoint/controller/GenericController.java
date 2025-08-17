package com.example.demo.touchpoint.controller;

import com.example.demo.touchpoint.entity.GlobalDefault;
import com.example.demo.touchpoint.repository.GlobalDefaultRepository;
import com.example.demo.core.service.GenericService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/entity")
public class GenericController {

    private final GenericService genericService;
    private final GlobalDefaultRepository globalDefaultRepository; // ví dụ repository

    public GenericController(GenericService genericService, GlobalDefaultRepository globalDefaultRepository) {
        this.genericService = genericService;
        this.globalDefaultRepository = globalDefaultRepository;
    }

    @GetMapping("/global_default")
    public String listGlobalDefault(Model model){
        List<GlobalDefault> list = genericService.findAll(globalDefaultRepository);
        List<Map<String,String>> cols = genericService.getColumns(GlobalDefault.class);

        model.addAttribute("tableName", genericService.getTableName(GlobalDefault.class));
        model.addAttribute("columns", cols);
        model.addAttribute("records", list);
        return "generic/list";
    }

    // form tạo mới
    @GetMapping("/global_default/create")
    public String createForm(Model model){
        List<Map<String,String>> cols = genericService.getColumns(GlobalDefault.class);
        model.addAttribute("columns", cols);
        model.addAttribute("record", new GlobalDefault());
        return "generic/form";
    }

    @PostMapping("/global_default/create")
    public String createSubmit(@ModelAttribute GlobalDefault record){
        globalDefaultRepository.insert(record);
        return "redirect:/admin/entity/global_default";
    }

    // form edit
    @GetMapping("/global_default/edit/{name}")
    public String editForm(@PathVariable String name, Model model){
        GlobalDefault record = globalDefaultRepository.findByName(name);
        model.addAttribute("columns", genericService.getColumns(GlobalDefault.class));
        model.addAttribute("record", record);
        return "generic/form";
    }

    @PostMapping("/global_default/edit/{id}")
    public String editSubmit(@PathVariable Long id, @ModelAttribute GlobalDefault record){
        record.setId(id);
        globalDefaultRepository.update(record);
        return "redirect:/admin/entity/global_default";
    }

    @PostMapping("/global_default/delete/{name}")
    public String delete(@PathVariable String name){
        globalDefaultRepository.deleteByName(name);
        return "redirect:/admin/entity/global_default";
    }
}
