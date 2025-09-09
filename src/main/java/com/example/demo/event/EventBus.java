package com.example.demo.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// EventBus hỗ trợ async
public class EventBus {
    private final Map<Class<?>, List<EventListener<?>>> listeners = new HashMap<Class<?>, List<EventListener<?>>>();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public <T> void subscribe(Class<T> eventType, EventListener<T> listener) {
        if (!listeners.containsKey(eventType)) {
            listeners.put(eventType, new ArrayList<EventListener<?>>());
        }
        listeners.get(eventType).add(listener);
    }

    public <T> void publish(final T event) {
        Class<?> eventType = event.getClass();
        List<EventListener<?>> subs = listeners.get(eventType);
        if (subs != null) {
            for (final EventListener<?> l : subs) {
                executor.submit(new Runnable() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void run() {
                        ((EventListener<T>) l).onEvent(event);
                    }
                });
            }
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}
