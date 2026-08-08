package com.streamforge.serviceimpl;

import com.streamforge.dto.response.NotificationResponse;
import com.streamforge.entity.Notification;
import com.streamforge.exception.ResourceNotFoundException;
import com.streamforge.mapper.NotificationMapper;
import com.streamforge.repository.NotificationRepository;
import com.streamforge.repository.UserRepository;
import com.streamforge.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final UserRepository userRepository;

    @Override
    public List<NotificationResponse> getUserNotifications(Long userId) {

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

    @Override
    public NotificationResponse markAsRead(Long notificationId) {

        Notification notification =
                notificationRepository.findById(notificationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notification not found with id: "
                                                + notificationId
                                )
                        );

        notification.setIsRead(true);

        return notificationMapper.toResponse(
                notificationRepository.save(notification)
        );
    }

    @Override
    public void deleteNotification(Long notificationId) {

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