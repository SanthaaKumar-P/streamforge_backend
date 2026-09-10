package com.streamforge.controller;

import com.streamforge.dto.response.NotificationResponse;
import com.streamforge.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;


    // =========================================================
    // GET USER NOTIFICATIONS
    // =========================================================
    //
    // The requested userId is checked inside the service
    // against the authenticated Spring Security user.
    //
    // Normal users can access only their own notifications.
    // ADMIN can access another user's notifications.
    // =========================================================

    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<NotificationResponse>>
    getUserNotifications(
            @PathVariable Long userId,
            Authentication authentication
    ) {

        List<NotificationResponse> notifications =
                notificationService.getUserNotifications(
                        userId,
                        authentication
                );

        return ResponseEntity.ok(
                notifications
        );
    }


    // =========================================================
    // MARK NOTIFICATION AS READ
    // =========================================================
    //
    // Only the notification owner can mark it as read.
    // ADMIN is also allowed.
    // Ownership is checked inside NotificationServiceImpl.
    // =========================================================

    @PutMapping("/{notificationId}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NotificationResponse>
    markAsRead(
            @PathVariable Long notificationId,
            Authentication authentication
    ) {

        NotificationResponse response =
                notificationService.markAsRead(
                        notificationId,
                        authentication
                );

        return ResponseEntity.ok(
                response
        );
    }


    // =========================================================
    // DELETE NOTIFICATION
    // =========================================================
    //
    // ADMIN ONLY.
    //
    // This matches the existing backend RBAC rule.
    // =========================================================

    @DeleteMapping("/{notificationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String>
    deleteNotification(
            @PathVariable Long notificationId
    ) {

        notificationService.deleteNotification(
                notificationId
        );

        return ResponseEntity.ok(
                "Notification deleted successfully"
        );
    }
}