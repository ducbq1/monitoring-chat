package com.example.demo.core.controller;

import com.example.demo.core.entity.GlobalDefault;
import com.example.demo.core.entity.RequestLog;
import com.example.demo.core.service.GenericService;
import com.example.demo.core.service.JdbcService;
import com.example.demo.core.service.ServiceFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/entity/log")
public class LogController extends BaseController<RequestLog> {
    public LogController(ServiceFactory serviceFactory,  GenericService genericService) {
        super(serviceFactory, genericService, RequestLog.class, "/admin/entity/log", "generic");
    }
}