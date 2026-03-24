package com.revshop.notificationservice.controller;

import com.revshop.notificationservice.model.Notification;
import com.revshop.notificationservice.service.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

//handle req
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationServiceImpl notificationService;

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(@RequestParam Long userId) {
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications); // get requ

    }
    
    @GetMapping("/buyer")
    public ResponseEntity<List<Notification>> getBuyerNotifications(@RequestHeader("X-User-Id") Long userId) {
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications); // get re

    }

    @GetMapping("/seller")
    public ResponseEntity<List<Notification>> getSellerNotifications(@RequestHeader("X-User-Id") Long userId) {
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications); // get req seller

    }

    @PutMapping("/{id}/read")
    public ResponseEntity<String> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok("Notification marked as read" ); // put requ

    }
    
    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestParam Long userId, @RequestParam String message) {
        notificationService.createNotification(userId, message);
        return ResponseEntity.ok("Notification sent" ); // post requ

    }
}
