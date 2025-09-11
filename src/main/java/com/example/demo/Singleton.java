package com.example.demo;

public class Singleton {
    private static Singleton instance;

    private Singleton() {}

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();  // Không đồng bộ hóa, không bảo vệ đối với đa luồng
        }
        return instance;
    }

    public void displayMessage() {
        System.out.println("Hello from Singleton!");
    }
}
