package com.streamforge.service;

import com.streamforge.dto.response.NotificationResponse;
import com.streamforge.enums.NotificationType;

import java.util.List;

public interface NotificationService {

    List<NotificationResponse> getUserNotifications(Long userId);

    NotificationResponse markAsRead(Long notificationId);

    void deleteNotification(Long notificationId);

    /*
     * Internal notification creation.
     * This is used by other backend modules such as
     * Show, Evaluation, Production and Reports.
     */
    NotificationResponse createNotification(
            Long userId,
            String title,
            String message,
            NotificationType notificationType
    );
}