package com.streamforge.service;

import com.streamforge.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {

    List<NotificationResponse> getUserNotifications(Long userId);

    NotificationResponse markAsRead(Long notificationId);

    void deleteNotification(Long notificationId);

}