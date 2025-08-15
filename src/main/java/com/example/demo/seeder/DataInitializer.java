package com.example.demo.seeder;

import com.example.demo.model.FeignLog;
import com.example.demo.model.UrlStatus;
import com.example.demo.repository.FeignLogRepository;
import com.example.demo.repository.UrlStatusRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer {

    private final UrlStatusRepository repository;
    private final FeignLogRepository feignLogRepo;

    public DataInitializer(UrlStatusRepository repository, FeignLogRepository feignLogRepo) {
        this.repository = repository;
        this.feignLogRepo = feignLogRepo;
    }

    @PostConstruct
    public void init() {

        // Kiểm tra file H2
        /*************
        File dbFile = new File("./data/chatdb.mv.db");
        if (dbFile.exists()) {
            System.out.println("DB file đã tồn tại -> bỏ qua khởi tạo dữ liệu mẫu.");
            return;
        }

        // Thêm dữ liệu mẫu nếu DB chưa tồn tại
        System.out.println("Khởi tạo dữ liệu mẫu vì DB chưa tồn tại...");
        // yourRepo.save(...);
        *************/

        if (repository.count() == 0) {
            List<String> urls = List.of(
                    "https://www.google.com",
                    "https://github.com",
                    "https://docs.spring.io",
                    "https://stackoverflow.com",
                    "https://openai.com",
                    "https://example.org", // tồn tại nhưng thường dùng để mô phỏng
                    "https://invalid-url.fake", // dùng để giả lập unreachable
                    "https://localhost:9999",   // mô phỏng service nội bộ chưa bật
                    "https://api.fake-service.local", // mô phỏng microservice không có thực
                    "https://your-internal-service.com/api/health" // giả lập endpoint nội bộ
            );

            for (String url : urls) {
                UrlStatus status = new UrlStatus();
                status.setUrl(url);
                status.setReachable(false); // mặc định chưa kiểm tra nên false
                status.setLastChecked(LocalDateTime.now());
                repository.save(status);
            }
        }

        if (feignLogRepo.count() == 0) {
            String[] methods = {"GET", "POST", "PUT", "DELETE"};
            String[] urls = {
                    "https://api.example.com/users",
                    "https://auth.service.internal/login",
                    "https://payment.service.local/transactions",
                    "https://inventory.service/api/products/42",
                    "https://external.api.io/search?q=laptop",
                    "https://gateway.local/internal/health",
                    "https://analytics.service/track",
                    "https://config.service/api/v1/configs",
                    "https://email.service/send",
                    "https://files.service/upload"
            };

            for (int i = 0; i < 10; i++) {
                String method = methods[i % methods.length];
                String url = urls[i];

                StringBuilder curl = new StringBuilder();
                curl.append("curl -X ").append(method).append(" \\\n");
                curl.append("  '").append(url).append("' \\\n");
                curl.append("  -H 'Authorization: Bearer <token>' \\\n");
                curl.append("  -H 'Content-Type: application/json'");

                if ("POST".equals(method) || "PUT".equals(method)) {
                    curl.append(" \\\n  -d '{\"id\":").append(i + 1)
                            .append(", \"name\":\"Item ").append(i + 1)
                            .append("\", \"active\":true}'");
                }

                FeignLog log = new FeignLog();
                log.setContent(curl.toString());
                log.setTime(LocalDateTime.now().minusMinutes(i * 5));
                feignLogRepo.save(log);
            }
        }
    }
}
