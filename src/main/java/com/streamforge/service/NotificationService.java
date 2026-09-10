package com.streamforge.service;

import com.streamforge.dto.response.NotificationResponse;
import com.streamforge.enums.NotificationType;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface NotificationService {

    // =========================================================
    // USER NOTIFICATIONS
    // =========================================================

    List<NotificationResponse> getUserNotifications(
            Long userId,
            Authentication authentication
    );


    // =========================================================
    // MARK AS READ
    // =========================================================

    NotificationResponse markAsRead(
            Long notificationId,
            Authentication authentication
    );


    // =========================================================
    // DELETE
    // =========================================================

    void deleteNotification(
            Long notificationId
    );


    // =========================================================
    // CREATE
    // =========================================================
    //
    // Internal method used by other modules:
    //
    // Show
    // Evaluation
    // Production
    // Reports
    // AI
    // etc.
    // =========================================================

    NotificationResponse createNotification(
            Long userId,
            String title,
            String message,
            NotificationType notificationType
    );
}