package com.example.demo.core.controller;

import com.example.demo.core.entity.GlobalDefault;
import com.example.demo.core.service.GenericService;
import com.example.demo.core.service.JdbcService;
import com.example.demo.core.service.ServiceFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

@Controller
@RequestMapping("/admin/entity/global_default")
public class GlobalDefaultController extends BaseController<GlobalDefault> {
    public GlobalDefaultController(ServiceFactory serviceFactory, GenericService genericService) {
        super(serviceFactory, genericService, GlobalDefault.class, "/admin/entity/global_default", "generic");
    }
}