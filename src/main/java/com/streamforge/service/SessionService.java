package com.streamforge.service;

import com.streamforge.dto.response.SessionResponse;

import java.util.List;

public interface SessionService {

    SessionResponse createSession(Long userId, String accessToken, String refreshToken);

    List<SessionResponse> getUserSessions(Long userId);

    void deactivateSession(Long sessionId);

}