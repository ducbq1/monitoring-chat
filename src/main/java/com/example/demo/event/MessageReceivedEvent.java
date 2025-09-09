package com.example.demo.event;

public class MessageReceivedEvent {
    public final String message;
    public MessageReceivedEvent(String msg) {
        this.message = msg;
    }
}
