package com.revshop.notificationservice.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;
    private boolean readStatus = false;
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "user_id")
    private Long userId;
}
