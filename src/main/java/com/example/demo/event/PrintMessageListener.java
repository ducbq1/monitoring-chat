package com.example.demo.event;

public class PrintMessageListener implements EventListener<MessageReceivedEvent> {
    @Override
    public void onEvent(MessageReceivedEvent event) {
        System.out.println("Handle message in async thread: " + event.message);
    }
}
