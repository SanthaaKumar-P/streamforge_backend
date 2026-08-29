package com.streamforge.serviceimpl;

import com.streamforge.dto.response.NotificationResponse;
import com.streamforge.entity.Notification;
import com.streamforge.entity.User;
import com.streamforge.enums.NotificationType;
import com.streamforge.exception.ResourceNotFoundException;
import com.streamforge.mapper.NotificationMapper;
import com.streamforge.repository.NotificationRepository;
import com.streamforge.repository.UserRepository;
import com.streamforge.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl
        implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final UserRepository userRepository;

    // =========================================================
    // CREATE NOTIFICATION
    // =========================================================

    @Override
    public NotificationResponse createNotification(
            Long userId,
            String title,
            String message,
            NotificationType notificationType
    ) {

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: "
                                                + userId
                                )
                        );

        Notification notification =
                Notification.builder()
                        .user(user)
                        .title(title)
                        .message(message)
                        .notificationType(notificationType)
                        .isRead(false)
                        .build();

        Notification saved =
                notificationRepository.save(notification);

        return notificationMapper.toResponse(saved);
    }

    // =========================================================
    // GET USER NOTIFICATIONS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUserNotifications(
            Long userId
    ) {

        if (!userRepository.existsById(userId)) {

            throw new ResourceNotFoundException(
                    "User not found with id: " + userId
            );
        }

        return notificationRepository
                .findByUserUserId(userId)
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    // =========================================================
    // MARK AS READ
    // =========================================================

    @Override
    public NotificationResponse markAsRead(
            Long notificationId
    ) {

        Notification notification =
                notificationRepository.findById(notificationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notification not found with id: "
                                                + notificationId
                                )
                        );

        notification.setIsRead(true);

        Notification saved =
                notificationRepository.save(notification);

        return notificationMapper.toResponse(saved);
    }

    // =========================================================
    // DELETE NOTIFICATION
    // =========================================================

    @Override
    public void deleteNotification(
            Long notificationId
    ) {

        Notification notification =
                notificationRepository.findById(notificationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notification not found with id: "
                                                + notificationId
                                )
                        );

        notificationRepository.delete(notification);
    }
}