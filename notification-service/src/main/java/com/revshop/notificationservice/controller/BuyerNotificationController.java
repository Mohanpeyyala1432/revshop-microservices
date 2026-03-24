package com.revshop.notificationservice.controller;

import com.revshop.notificationservice.model.Notification;
import com.revshop.notificationservice.service.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// mapping to controller
@RestController
@RequestMapping("/api/buyer/notifications")
@RequiredArgsConstructor
public class BuyerNotificationController { // directs

    private final NotificationServiceImpl notificationService;

    @GetMapping
    public ResponseEntity<List<Notification>> getBuyerNotifications(@RequestHeader("X-User-Id") Long userId) {
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications); // get req
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<String> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok("Notification marked as read" ); // put req
    }
}
