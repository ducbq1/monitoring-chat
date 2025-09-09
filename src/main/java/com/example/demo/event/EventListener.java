package com.example.demo.event;

// Interface cho listener
interface EventListener<T> {
    void onEvent(T event);
}
