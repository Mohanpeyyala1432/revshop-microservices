package com.revshop.notificationservice.service;

import com.revshop.notificationservice.model.Notification;
import com.revshop.notificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Notification notification;

    @BeforeEach
    void setUp() {
        notification = new Notification();
        notification.setId(1L);
        notification.setUserId(1L);
        notification.setMessage("Test Notification");
        notification.setReadStatus(false);
    }

    @Test
    void createNotification_WithNotificationObject_Success() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        Notification result = notificationService.createNotification(notification);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("Test Notification", result.getMessage());
        verify(notificationRepository).save(notification);
    }

    @Test
    void getNotificationsByUserId_Success() {
        List<Notification> notifications = Arrays.asList(notification);
        when(notificationRepository.findByUserId(1L)).thenReturn(notifications);

        List<Notification> result = notificationService.getNotificationsByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getUserId());
    }

    @Test
    void getUserNotifications_Success() {
        // This method appears to be a duplicate of getNotificationsByUserId in the implementation
        List<Notification> notifications = Arrays.asList(notification);
        when(notificationRepository.findByUserId(1L)).thenReturn(notifications);

        List<Notification> result = notificationService.getUserNotifications(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getUserId());
    }

    @Test
    void markAsRead_Success() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        notificationService.markAsRead(1L);

        assertTrue(notification.isReadStatus());
        verify(notificationRepository).save(notification);
    }

    @Test
    void markAsRead_NotificationNotFound_ThrowsException() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> notificationService.markAsRead(1L));
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void createNotification_WithUserIdAndMessage_Success() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        notificationService.createNotification(1L, "Test Message");

        verify(notificationRepository).save(any(Notification.class));
    }
}
