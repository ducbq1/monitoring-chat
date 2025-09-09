package com.example.demo;

import com.example.demo.event.EventBus;
import com.example.demo.event.MessageReceivedEvent;

import javax.websocket.*;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MyWebSocketClient extends Endpoint {

    private static final long RETRY_INTERVAL = 5000;  // Retry interval (5 seconds)
    private static WebSocketContainer container;
    private static Session session;
    private static EventBus eventBus;

    // Retry count to keep track of the number of attempts
    private static int retryCount = 0;

    public static void setEventBus(EventBus bus) {
        eventBus = bus;
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("Connected to server");

        // Store the session object globally to use later
        MyWebSocketClient.session = session;

        // Send a message to the server upon successful connection
        try {
            session.getBasicRemote().sendText("Hello from client");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Register message handler for receiving String messages
        session.addMessageHandler(String.class, new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                eventBus.publish(new MessageReceivedEvent(message));
                System.out.println("Received String message: " + message);
            }
        });

        // Register message handler for receiving ByteBuffer messages
        session.addMessageHandler(ByteBuffer.class, message -> System.out.println("Received ByteBuffer message: " + new String(message.array())));
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("Connection closed: " + closeReason);

        // Retry to reconnect when the connection is closed unexpectedly
        retryConnection();
    }

    @Override
    public void onError(Session session, Throwable t) {
        t.printStackTrace();

        // Retry to reconnect if an error occurs
        retryConnection();
    }

    // Method to send a message to the server
    public static void sendMessage(String message) {
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
                System.out.println("Sent message: " + message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Session is not open, cannot send message.");
        }
    }

    // Method for permanent retry mechanism if connection fails or is closed unexpectedly
    public static void retryConnection() {
        while (true) {  // Infinite retry loop
            try {
                System.out.println("Attempting to reconnect...");

                // If session is closed or null, attempt to reconnect
                if (session == null || !session.isOpen()) {
                    container = ContainerProvider.getWebSocketContainer();
                    URI uri = new URI("ws://localhost:9090/ws");

                    // Create a custom header configuration
                    String jwtToken = AuthHelper.getToken("steve", "GDV", "App1", "api-key-1");
                    ClientEndpointConfig.Configurator configurator = new ClientEndpointConfig.Configurator() {
                        @Override
                        public void beforeRequest(Map<String, List<String>> headers) {
                            // Add Authorization and Custom headers
                            headers.put("Authorization", Collections.singletonList("Bearer " + jwtToken));
                            headers.put("X-Custom-Header", Collections.singletonList("custom_value"));
                        }
                    };

                    ClientEndpointConfig config = ClientEndpointConfig.Builder.create()
                            .configurator(configurator)
                            .build();

                    // Attempt to connect with custom headers
                    container.connectToServer(MyWebSocketClient.class, config, uri);

                    // Wait for the connection to be established
                    TimeUnit.MILLISECONDS.sleep(RETRY_INTERVAL);  // Sleep before retrying
                    break;  // If connection is successful, break out of the loop
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Wait before retrying
                System.out.println("Retrying in " + RETRY_INTERVAL / 1000 + " seconds...");
                try {
                    TimeUnit.MILLISECONDS.sleep(RETRY_INTERVAL);  // Wait before retrying
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }
    }
}
