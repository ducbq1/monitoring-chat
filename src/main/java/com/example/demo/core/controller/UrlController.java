package com.example.demo.core.controller;

import com.example.demo.core.entity.inquiry.UrlStatus;
import com.example.demo.core.service.ServiceFactory;
import com.example.demo.helper.FieldUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/entity/url")
public class UrlController extends BaseController<UrlStatus> {
    public UrlController(ServiceFactory serviceFactory, FieldUtil fieldUtil) {
        super(serviceFactory, fieldUtil, UrlStatus.class, "/admin/entity/url", "generic");
    }
}
