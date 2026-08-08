package com.streamforge.serviceimpl;

import com.streamforge.dto.response.SessionResponse;
import com.streamforge.entity.Session;
import com.streamforge.entity.User;
import com.streamforge.exception.ResourceNotFoundException;
import com.streamforge.mapper.SessionMapper;
import com.streamforge.repository.SessionRepository;
import com.streamforge.repository.UserRepository;
import com.streamforge.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final SessionMapper sessionMapper;

    @Override
    public SessionResponse createSession(
            Long userId,
            String accessToken,
            String refreshToken
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + userId
                        )
                );

        Session session = Session.builder()
                .user(user)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .loginTime(LocalDateTime.now())
                .expiryTime(
                        LocalDateTime.now().plusDays(7)
                )
                .isActive(true)
                .build();

        return sessionMapper.toResponse(
                sessionRepository.save(session)
        );
    }

    @Override
    public List<SessionResponse> getUserSessions(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User not found with id: " + userId
            );
        }

        return sessionRepository.findAll()
                .stream()
                .filter(
                        session -> session.getUser()
                                .getUserId()
                                .equals(userId)
                )
                .map(sessionMapper::toResponse)
                .toList();
    }

    @Override
    public void deactivateSession(Long sessionId) {

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Session not found with id: " + sessionId
                        )
                );

        session.setIsActive(false);

        sessionRepository.save(session);
    }
}