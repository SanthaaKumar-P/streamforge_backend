package com.streamforge.mapper;

import com.streamforge.dto.response.SessionResponse;
import com.streamforge.entity.Session;
import org.springframework.stereotype.Component;

@Component
public class SessionMapper {


    public SessionResponse toResponse(Session session){

        return SessionResponse.builder()
                .sessionId(session.getSessionId())
                .deviceInfo(session.getDeviceInfo())
                .ipAddress(session.getIpAddress())
                .loginTime(session.getLoginTime())
                .expiryTime(session.getExpiryTime())
                .isActive(session.getIsActive())
                .build();

    }

}