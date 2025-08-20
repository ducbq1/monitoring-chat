package com.example.demo.exception;

import com.example.demo.core.model.MessageDTO;
import com.example.demo.core.repository.ViewContext;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalErrorHandler {

    private final ViewContext viewContext;

    public GlobalErrorHandler(ViewContext viewContext) {
        this.viewContext = viewContext;
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public String handleEmptyResult(EmptyResultDataAccessException ex, Model model) {
        model.addAttribute("error", "Không tìm thấy dữ liệu phù hợp.");
        return resolveView();
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public String handleDuplicateKey(DuplicateKeyException ex, Model model) {
        model.addAttribute("error", "Dữ liệu đã tồn tại và không thể chèn trùng.");
        return resolveView();
    }

    @ExceptionHandler(BadSqlGrammarException.class)
    public String handleBadSql(BadSqlGrammarException ex, Model model) {
        model.addAttribute("error", "Lỗi cú pháp SQL. Vui lòng liên hệ quản trị viên.");
        return resolveView();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrity(DataIntegrityViolationException ex, Model model) {
        model.addAttribute("error", "Vi phạm ràng buộc dữ liệu (FK, NOT NULL, UNIQUE...).");
        return resolveView();
    }

    @ExceptionHandler(CannotGetJdbcConnectionException.class)
    public String handleJdbcConnection(CannotGetJdbcConnectionException ex, Model model) {
        model.addAttribute("error", "Không thể kết nối đến cơ sở dữ liệu.");
        return resolveView();
    }

    @ExceptionHandler(DataAccessException.class)
    public String handleGeneralDataAccess(DataAccessException ex, Model model) {
        model.addAttribute("error", "Lỗi truy vấn cơ sở dữ liệu.");
        return resolveView();
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handle404() {
        return "error/404";
    }

    @ExceptionHandler(AppException.class)
    public String handle(AppException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", MessageDTO.error(ex.getMessage(), ex.getCause().getMessage()));
        return "redirect:" + resolveView();
    }

    @ExceptionHandler(RuntimeException.class)
    public String handleGenericException(RuntimeException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/500";
    }

    private String resolveView() {
        String view = viewContext.getCurrentView();
        return (view != null && !view.isEmpty()) ? view : "error/500";
    }
}
