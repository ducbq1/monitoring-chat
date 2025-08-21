package com.example.demo.core.controller;

import com.example.demo.core.model.MessageDTO;
import com.example.demo.core.repository.ErrorHolder;
import com.example.demo.helper.ViewHelper;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.ui.Model;

import java.util.Locale;

public abstract class EntityLookupBaseController {
    private final ErrorHolder errorHolder;
    private final MessageSource messageSource;

    protected EntityLookupBaseController(ErrorHolder errorHolder, MessageSource messageSource) {
        this.errorHolder = errorHolder;
        this.messageSource = messageSource;
    }

    protected String render(Model model, String viewName, String title) {
        Locale locale = LocaleContextHolder.getLocale();
        if (errorHolder.hasError()) {
            model.addAttribute("message",
                    MessageDTO.error(
                            messageSource.getMessage(errorHolder.getTitle(), null, locale),
                            messageSource.getMessage(errorHolder.getMessage(), null, locale
                            )));
        }
        ViewHelper.setView(model, viewName, title);
        return "layout";
    }
}
