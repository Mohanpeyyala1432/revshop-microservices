package com.revshop.notificationservice.repository;

import com.revshop.notificationservice.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,  Long> { //j p a
    List<Notification> findByUserId(Long userId) ;

}
