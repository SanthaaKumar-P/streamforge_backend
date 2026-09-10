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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
                userRepository
                        .findById(userId)
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
                        .notificationType(
                                notificationType
                        )
                        .isRead(false)
                        .build();


        Notification saved =
                notificationRepository.save(
                        notification
                );


        return notificationMapper.toResponse(
                saved
        );
    }


    // =========================================================
    // GET USER NOTIFICATIONS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse>
    getUserNotifications(
            Long userId,
            Authentication authentication
    ) {

        /*
         * Resolve the actual authenticated user.
         */
        User authenticatedUser =
                getAuthenticatedUser(
                        authentication
                );


        /*
         * IMPORTANT SECURITY CHECK
         *
         * Prevent:
         *
         * User 5
         *   ↓
         * /api/notifications/user/1
         *
         * from reading User 1's notifications.
         */

        if (
                !authenticatedUser
                        .getUserId()
                        .equals(userId)
        ) {

            /*
             * Admin can optionally access other users'
             * notification lists for administrative purposes.
             *
             * Normal users cannot.
             */

            if (!isAdmin(authentication)) {

                throw new AccessDeniedException(
                        "You are not allowed to access another user's notifications."
                );
            }
        }


        /*
         * Make sure requested user exists.
         */

        if (
                !userRepository.existsById(
                        userId
                )
        ) {

            throw new ResourceNotFoundException(
                    "User not found with id: "
                            + userId
            );
        }


        /*
         * Fetch notifications.
         */

        return notificationRepository
                .findByUserUserId(
                        userId
                )
                .stream()
                .map(
                        notificationMapper::toResponse
                )
                .toList();
    }


    // =========================================================
    // MARK AS READ
    // =========================================================

    @Override
    public NotificationResponse markAsRead(
            Long notificationId,
            Authentication authentication
    ) {

        Notification notification =
                notificationRepository
                        .findById(
                                notificationId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notification not found with id: "
                                                + notificationId
                                )
                        );


        /*
         * Resolve authenticated user.
         */

        User authenticatedUser =
                getAuthenticatedUser(
                        authentication
                );


        /*
         * Only the owner can mark the notification
         * as read.
         *
         * Admin can also perform the operation.
         */

        boolean owner =
                notification
                        .getUser()
                        .getUserId()
                        .equals(
                                authenticatedUser
                                        .getUserId()
                        );


        if (
                !owner &&
                !isAdmin(authentication)
        ) {

            throw new AccessDeniedException(
                    "You are not allowed to modify another user's notification."
            );
        }


        /*
         * Already read?
         *
         * Keeping this idempotent makes the frontend
         * simpler and avoids unnecessary errors.
         */

        if (
                !Boolean.TRUE.equals(
                        notification.getIsRead()
                )
        ) {

            notification.setIsRead(
                    true
            );
        }


        Notification saved =
                notificationRepository.save(
                        notification
                );


        return notificationMapper.toResponse(
                saved
        );
    }


    // =========================================================
    // DELETE NOTIFICATION
    // =========================================================
    //
    // Controller already protects this endpoint with:
    //
    // @PreAuthorize("hasRole('ADMIN')")
    //
    // Therefore this method does not need another
    // authentication check.
    // =========================================================

    @Override
    public void deleteNotification(
            Long notificationId
    ) {

        Notification notification =
                notificationRepository
                        .findById(
                                notificationId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notification not found with id: "
                                                + notificationId
                                )
                        );


        notificationRepository.delete(
                notification
        );
    }


    // =========================================================
    // AUTHENTICATED USER RESOLUTION
    // =========================================================

    private User getAuthenticatedUser(
            Authentication authentication
    ) {

        if (
                authentication == null ||
                !authentication.isAuthenticated()
        ) {

            throw new AccessDeniedException(
                    "Authentication is required."
            );
        }


        Object principal =
                authentication.getPrincipal();


        /*
         * Your JwtAuthenticationFilter creates:
         *
         * UsernamePasswordAuthenticationToken(
         *      userDetails,
         *      null,
         *      authorities
         * )
         *
         * Therefore the normal principal is
         * UserDetails / CustomUserDetails.
         */

        String username = null;


        if (
                principal instanceof UserDetails
        ) {

            username =
                    (
                            (UserDetails)
                                    principal
                    )
                            .getUsername();

        } else if (
                principal instanceof String
        ) {

            username =
                    (String)
                            principal;
        }


        if (
                username == null ||
                username.isBlank()
        ) {

            throw new AccessDeniedException(
                    "Unable to determine authenticated user."
            );
        }


        /*
         * Your CustomUserDetailsService supports both
         * username and email, so resolve both safely.
         */

        final String credential =
                username.trim();


        return userRepository
                .findByUsername(
                        credential
                )
                .or(() ->
                        userRepository
                                .findByEmail(
                                        credential
                                )
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found."
                        )
                );
    }


    // =========================================================
    // ADMIN CHECK
    // =========================================================

    private boolean isAdmin(
            Authentication authentication
    ) {

        if (
                authentication == null
        ) {

            return false;
        }


        return authentication
                .getAuthorities()
                .stream()
                .anyMatch(
                        authority ->
                                "ROLE_ADMIN"
                                        .equals(
                                                authority
                                                        .getAuthority()
                                        )
                );
    }
}