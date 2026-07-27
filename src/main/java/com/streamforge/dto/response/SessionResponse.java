package com.streamforge.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionResponse {

    private Long sessionId;

    private String deviceInfo;

    private String ipAddress;

    private LocalDateTime loginTime;

    private LocalDateTime expiryTime;

    private Boolean isActive;

}