package com.example.demo.core.controller;

import com.example.demo.core.entity.normal.GlobalDefault;
import com.example.demo.core.service.ServiceFactory;
import com.example.demo.helper.FieldUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

@Controller
@RequestMapping("/admin/entity/global_default")
public class GlobalDefaultController extends BaseController<GlobalDefault> {
    public GlobalDefaultController(ServiceFactory serviceFactory, FieldUtil fieldUtil) {
        super(serviceFactory, fieldUtil, GlobalDefault.class, "/admin/entity/global_default", "generic");
    }
}
