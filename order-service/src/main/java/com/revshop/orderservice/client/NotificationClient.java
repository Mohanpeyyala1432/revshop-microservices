package com.revshop.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "notification-service")
public interface NotificationClient {
    @PostMapping("/api/notifications/send")
    void sendNotification(@RequestParam Long userId, @RequestParam String message);
}
