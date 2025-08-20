package com.example.demo.config;

import com.example.demo.model.FeignLog;
import com.example.demo.model.UrlStatus;
import com.example.demo.repository.FeignLogRepository;
import com.example.demo.repository.UrlStatusRepository;
import com.example.demo.service.HealthCheckService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CheckError {

    private final SimpMessagingTemplate messagingTemplate;
    private final UrlStatusRepository urlStatusRepository;
    private final FeignLogRepository feignLogRepository;
    private final HealthCheckService healthCheckService;

    public CheckError(SimpMessagingTemplate messagingTemplate, UrlStatusRepository urlStatusRepository, FeignLogRepository feignLogRepository, HealthCheckService healthCheckService) {
        this.messagingTemplate = messagingTemplate;
        this.urlStatusRepository = urlStatusRepository;
        this.feignLogRepository = feignLogRepository;
        this.healthCheckService = healthCheckService;
    }

    @Scheduled(fixedRate = 30 * 1000)
    public void check() {
        healthCheckService.checkAll();
    }

    @Scheduled(fixedRate = 30 * 1000) // mỗi 30s
    public void checkErrors() {
        List<UrlStatus> unreachable = urlStatusRepository.findAllByReachableFalse();
        List<FeignLog> feignErrors = feignLogRepository.findAll();

        List<String> notifications = new ArrayList<>();

        for (UrlStatus url : unreachable) {
            notifications.add("URL lỗi: " + url.getUrl());
        }

        for (FeignLog log : feignErrors) {
            notifications.add("Feign lỗi: " + log.getId() + " - " + log.getTime());
        }

        for (String message : notifications) {
            messagingTemplate.convertAndSend("/topic/alerts", message);
        }
    }

}
