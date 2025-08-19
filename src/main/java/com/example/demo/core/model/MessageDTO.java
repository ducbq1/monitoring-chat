package com.example.demo.core.model;

public class MessageDTO {
    private String type;
    private String title;
    private String detail;

    public MessageDTO(String type, String title, String detail) {
        this.type = type;
        this.title = title;
        this.detail = detail;
    }

    public static MessageDTO success(String title, String detail) {
        return new MessageDTO("success", title, detail);
    }

    public static MessageDTO error(String title, String detail) {
        return new MessageDTO("error", title, detail);
    }

    public static MessageDTO info(String title, String detail) {
        return new MessageDTO("info", title, detail);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String message) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
