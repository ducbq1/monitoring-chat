package com.example.demo;

import com.example.demo.config.DatabaseProperties;
import com.example.demo.config.DynamicDataSourceConfig;
import com.example.demo.event.EventBus;
import com.example.demo.event.MessageReceivedEvent;
import com.example.demo.event.PrintMessageListener;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.ItemView;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.Base64;

@SpringBootApplication
@EnableScheduling
@EnableRetry
public class DemoApplication {

    private static final String HOST = "localhost";
    private static final int PORT = 9090;
    private static final String PATH = "/ws";


    public static void main(String[] args) throws Exception {
        SpringApplication.run(DemoApplication.class, args);
        EventBus bus = new EventBus();
        bus.subscribe(MessageReceivedEvent.class, new PrintMessageListener());
        new Thread(() -> {
            MyWebSocketClient.setEventBus(bus);
            MyWebSocketClient.retryConnection();  // Try to connect with retry mechanism
            // Send a message after a successful connection
            MyWebSocketClient.sendMessage("Hello from the client after retry!");
        }).start();
    }

    private void connect_2() throws InterruptedException, IOException {
        EventBus bus = new EventBus();
        bus.subscribe(MessageReceivedEvent.class, new PrintMessageListener());

        String jwtToken = null;
        try {
            jwtToken = AuthHelper.getToken("steve", "GDV", "App1", "api-key-1");
        } catch (Exception e) {
            System.err.println("Get token failed " + e.getMessage());
        }

        WebSocketClient client = new WebSocketClient();
        client.tryReconnect(jwtToken, bus);

        // send multiple messages
        client.send("Hello 1");
        client.send("Hello 2");
        client.send("Hello 3");

        // keep alive
        Thread.sleep(10000);
        client.close();
    }

    private void connect_1() throws Exception {
        String jwtToken = null;
        try {
            jwtToken = AuthHelper.getToken("steve", "GDV", "App1", "api-key-1");
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true) { // vòng lặp reconnect mãi mãi
            try {
                connectAndListen(jwtToken);
            } catch (Exception e) {
                if (e.getMessage().contains("401") || e.getMessage().contains("Unauthorized")) {
                    AuthHelper.invalidateToken();
                    String newToken = AuthHelper.getToken("steve", "GDV", "App1", "api-key-1");
                    connectAndListen(newToken);
                } else {
                    System.out.println("Mất kết nối: " + e.getMessage());
                }
            }

            // chờ 3s rồi reconnect
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignore) {
            }
        }
    }

    @Bean
    public DynamicDataSourceConfig dynamicDataSourceConfig(DatabaseProperties databaseProperties) {
        return new DynamicDataSourceConfig(databaseProperties);
    }

    private void connectMail() throws Exception {
        // 1. Tạo service
        ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);

        // 2. Đăng nhập (Office 365 / Outlook.com hỗ trợ Basic Auth nếu bật, hoặc OAuth2)
        service.setCredentials(new WebCredentials("duc.bq173023@outlook.com", "Danshari7954"));

        // 3. Chỉ định endpoint
        service.setUrl(new URI("https://outlook.office365.com/EWS/Exchange.asmx"));

        // 4. Lấy folder Inbox
        Folder inbox = Folder.bind(service, WellKnownFolderName.Inbox);

        System.out.println("Total count: " + inbox.getTotalCount());

        // 5. Lấy mail trong Inbox
        ItemView view = new ItemView(10); // lấy 10 mail mới nhất
        FindItemsResults<Item> findResults =
                service.findItems(WellKnownFolderName.Inbox, view);

        for (Item item : findResults) {
            if (item instanceof EmailMessage) {
                EmailMessage email = (EmailMessage) item;
                email.load(); // load chi tiết
                System.out.println("Subject: " + email.getSubject());
                System.out.println("From: " + email.getFrom().getAddress());
                System.out.println("Date: " + email.getDateTimeReceived());
                System.out.println("----------------------");
            }
        }
    }

    private static void connectAndListen(String jwtToken) throws Exception {
        Socket socket = new Socket(HOST, PORT);
        OutputStream out = socket.getOutputStream();
        InputStream in = socket.getInputStream();

        // tạo Sec-WebSocket-Key hợp lệ
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
                        "Authorization: Bearer " + jwtToken + "\r\n" +
                        "Sec-WebSocket-Version: 13\r\n\r\n";
        out.write(handshake.getBytes("UTF-8"));
        out.flush();

        // Đọc response trước
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
            throw new IOException("Handshake failed, không thể upgrade WebSocket");
        }

        // gửi message text thử
        sendText(out, "Hello Go server!");

        // vòng lặp đọc message mãi
        while (true) {
            int b1 = in.read();
            if (b1 == -1) throw new IOException("Stream closed");

            int b2 = in.read();
            if (b2 == -1) throw new IOException("Stream closed");

            boolean fin = (b1 & 0x80) != 0;
            int opcode = b1 & 0x0F;
            boolean masked = (b2 & 0x80) != 0;
            int payloadLen = b2 & 0x7F;

            if (payloadLen == 126) {
                payloadLen = (in.read() << 8) | in.read();
            } else if (payloadLen == 127) {
                // bỏ qua cho demo
                throw new IOException("Payload quá dài cho ví dụ này");
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
                if (r == -1) throw new IOException("Stream closed sớm");
                read += r;
            }

            if (masked && mask != null) {
                for (int i = 0; i < payloadLen; i++) {
                    payload[i] ^= mask[i % 4];
                }
            }

            if (opcode == 1) { // text frame
                String msg = new String(payload, "UTF-8");
                System.out.println("Recv: " + msg);
            } else if (opcode == 8) {
                System.out.println("Server đóng kết nối");
                break;
            } else {
                System.out.println("Opcode khác: " + opcode);
            }
        }

    }

    private static void sendText(OutputStream out, String message) throws IOException {
        byte[] data = message.getBytes("UTF-8");
        int frameLen = data.length;

        out.write(0x81); // FIN=1, opcode=1 (text)

        if (frameLen <= 125) {
            out.write(0x80 | frameLen); // set MASK bit (0x80)
        } else if (frameLen <= 65535) {
            out.write(0x80 | 126);
            out.write((frameLen >> 8) & 0xFF);
            out.write(frameLen & 0xFF);
        } else {
            throw new IOException("Payload too large");
        }

        // Mask key (random 4 bytes)
        byte[] mask = new byte[4];
        new java.util.Random().nextBytes(mask);
        out.write(mask);

        // Apply mask (XOR each byte)
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (data[i] ^ mask[i % 4]);
        }

        out.write(data);
        out.flush();
    }


}
