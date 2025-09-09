package com.example.demo;

import org.msgpack.MessagePack;
import org.msgpack.template.Templates;

import javax.swing.*;
import java.io.IOException;

public class Utilities {

    private String text;
    private volatile boolean running = true;

    public void SwingTest() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setText("Updated safely");
            }
        });
    }

    private void setText(String text) {
        this.text = text;
    }

    public void stop() {
        running = false;
    }

    public void run() {
        while (running) {

        }
        System.out.println("Stopped");
    }

    public class Person {
        public String name;
        public String email;
    }

    private void MessagePack() throws IOException {
        byte[] bytes = new byte[1024];
        MessagePack msgpack = new MessagePack();
        String result = msgpack.read(bytes, Templates.TString);
        Person person = msgpack.read(bytes, Person.class);
    }
}
