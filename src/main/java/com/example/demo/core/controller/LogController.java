package com.example.demo.core.controller;

import com.example.demo.core.entity.normal.RequestLog;
import com.example.demo.core.service.ServiceFactory;
import com.example.demo.helper.FieldUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/entity/log")
public class LogController extends BaseController<RequestLog> {
    public LogController(ServiceFactory serviceFactory,  FieldUtil fieldUtil) {
        super(serviceFactory, fieldUtil, RequestLog.class, "/admin/entity/log", "generic");
    }
}
