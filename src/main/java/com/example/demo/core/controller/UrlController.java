package com.example.demo.core.controller;

import com.example.demo.core.entity.RequestLog;
import com.example.demo.core.entity.UrlStatus;
import com.example.demo.core.service.GenericService;
import com.example.demo.core.service.ServiceFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/entity/url")
public class UrlController extends BaseController<UrlStatus> {
    public UrlController(ServiceFactory serviceFactory, GenericService genericService) {
        super(serviceFactory, genericService, UrlStatus.class, "/admin/entity/url", "generic");
    }
}