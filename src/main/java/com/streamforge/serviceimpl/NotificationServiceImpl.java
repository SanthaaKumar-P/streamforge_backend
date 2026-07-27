package com.streamforge.serviceimpl;

import com.streamforge.dto.response.NotificationResponse;
import com.streamforge.entity.Notification;
import com.streamforge.mapper.NotificationMapper;
import com.streamforge.repository.NotificationRepository;
import com.streamforge.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {


    private final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;


    @Override
    public List<NotificationResponse> getUserNotifications(Long userId) {

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
                .orElseThrow(
                        () -> new RuntimeException("Notification not found")
                );


        notification.setIsRead(true);


        return notificationMapper.toResponse(
                notificationRepository.save(notification)
        );

    }


    @Override
    public void deleteNotification(Long notificationId) {

        notificationRepository.deleteById(notificationId);

    }

}