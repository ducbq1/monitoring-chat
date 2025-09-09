package com.example.demo;

import com.example.demo.event.EventBus;
import com.example.demo.event.MessageReceivedEvent;
import jdk.jfr.Event;

import java.io.*;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WebSocketClient {
    private static final String HOST = "localhost";
    private static final int PORT = 9090;
    private static final String PATH = "/ws";
    private Socket socket;
    private OutputStream out;
    private InputStream in;
    private Thread readerThread;
    private Thread senderThread;
    private BlockingQueue<String> sendQueue = new LinkedBlockingQueue<String>();
    private volatile boolean running = true;

    public void tryReconnect(String jwtToken, EventBus bus) {
        int retryDelay = 3000;

        while (running) {
            try {
                connect(jwtToken, bus);
                System.out.println("Reconnect successful.");
                return; // thành công thì thoát luôn

            } catch (Exception ex) {
                System.err.println("Reconnect failed: " + ex.getMessage());

                // Nếu lỗi 401 → xin lại token
                if (ex.getMessage().contains("Handshake failed, cannot upgrade WebSocket")) {
                    AuthHelper.invalidateToken();
                    try {
                        jwtToken = AuthHelper.getToken("steve", "role", "App1", "api-key-1");
                    } catch (Exception e) {
                        System.err.println("Get token failed after invalidation: " + e.getMessage());
                    }
                }

                try {
                    Thread.sleep(retryDelay);
                } catch (InterruptedException ignore) {

                }
            }
        }
    }


    public void connect(String jwtToken, EventBus bus) throws Exception {
        socket = new Socket(HOST, PORT);
        out = socket.getOutputStream();
        in = socket.getInputStream();

        // tạo Sec-WebSocket-Key
        byte[] nonce = new byte[16];
        new SecureRandom().nextBytes(nonce);
        String key = Base64.getEncoder().encodeToString(nonce);

        // gửi HTTP handshake
        String handshake =
                "GET " + PATH + " HTTP/1.1\r\n" +
                        "Host: " + HOST + ":" + PORT + "\r\n" +
                        "Upgrade: websocket\r\n" +
                        "Connection: Upgrade\r\n" +
                        "Sec-WebSocket-Key: " + key + "\r\n" +
                        "Sec-WebSocket-Version: 13\r\n" +
                        "Authorization: Bearer " + jwtToken + "\r\n\r\n";

        out.write(handshake.getBytes("UTF-8"));
        out.flush();

        // đọc response handshake
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        boolean success = false;
        while (!(line = reader.readLine()).isEmpty()) {
            System.out.println(line);
            if (line.startsWith("HTTP/1.1 101")) {
                success = true;
            }
        }
        if (!success) {
            throw new IOException("Handshake failed, cannot upgrade WebSocket");
        }

        // start reader thread
        startReaderThread(jwtToken, bus);

        // start sender thread
        startSenderThread();
    }

    private void startReaderThread(final String jwtToken, EventBus bus) {
        readerThread = new Thread(new Runnable() {
            public void run() {
                try {
                    while (running) {
                        int b1 = in.read();
                        if (b1 == -1) break;
                        int b2 = in.read();
                        if (b2 == -1) break;

                        boolean fin = (b1 & 0x80) != 0;
                        int opcode = b1 & 0x0F;
                        boolean masked = (b2 & 0x80) != 0;
                        int payloadLen = b2 & 0x7F;

                        if (payloadLen == 126) {
                            payloadLen = (in.read() << 8) | in.read();
                        } else if (payloadLen == 127) {
                            throw new IOException("Payload too long");
                        }

                        byte[] mask = null;
                        if (masked) {
                            mask = new byte[4];
                            in.read(mask);
                        }

                        byte[] payload = new byte[payloadLen];
                        int read = 0;
                        while (read < payloadLen) {
                            int r = in.read(payload, read, payloadLen - read);
                            if (r == -1) throw new IOException("Stream closed early");
                            read += r;
                        }

                        if (masked && mask != null) {
                            for (int i = 0; i < payloadLen; i++) {
                                payload[i] ^= mask[i % 4];
                            }
                        }

                        if (opcode == 1) { // text frame
                            String msg = new String(payload, "UTF-8");
                            bus.publish(new MessageReceivedEvent(msg));
                            System.out.println("Recv: " + msg);
                        } else if (opcode == 8) { // close
                            System.out.println("Server closed connection");
                            break;
                        } else {
                            System.out.println("Other opcode: " + opcode);
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Reader error: " + e.getMessage());
                    try {
                        close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    tryReconnect(jwtToken, bus);
                }
            }
        });
        readerThread.setDaemon(true);
        readerThread.start();
    }

    private void startSenderThread() {
        senderThread = new Thread(new Runnable() {
            public void run() {
                try {
                    while (running) {
                        String msg = sendQueue.take(); // block until message
                        sendText(msg);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        senderThread.setDaemon(true);
        senderThread.start();
    }

    public void send(String message) throws InterruptedException {
        sendQueue.put(message);
    }

    private void sendText(String message) throws IOException {
        byte[] payload = message.getBytes("UTF-8");
        int frameLen = payload.length;

        out.write(0x81); // FIN=1, opcode=1 (text)

        if (frameLen <= 125) {
            out.write(0x80 | frameLen); // set MASK bit (0x80)
        } else if (frameLen <= 65535) {
            out.write(0x80 | 126);
            out.write((frameLen >> 8) & 0xFF);
            out.write(frameLen & 0xFF);
        } else {
            out.write(0x80 | 127); // MASK=1 + special length code 127
            for (int i = 7; i >= 0; i--) {
                out.write((frameLen >> (8 * i)) & 0xFF);
            }
            // throw new IOException("Payload too large");
        }

        // Mask key (random 4 bytes)
        byte[] mask = new byte[4];
        new java.util.Random().nextBytes(mask);
        out.write(mask);

        // Apply mask (XOR each byte)
        for (int i = 0; i < payload.length; i++) {
            payload[i] = (byte) (payload[i] ^ mask[i % 4]);
        }

        out.write(payload);
        out.flush();
    }

    public void close() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        if (readerThread != null && readerThread.isAlive()) {
            readerThread.interrupt();
        }
        if (senderThread != null && senderThread.isAlive()) {
            senderThread.interrupt();
        }
    }
}
