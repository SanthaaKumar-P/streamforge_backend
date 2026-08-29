package com.streamforge.serviceimpl;

import com.streamforge.dto.response.AuditLogResponse;
import com.streamforge.entity.AuditLog;
import com.streamforge.entity.User;
import com.streamforge.exception.ResourceNotFoundException;
import com.streamforge.mapper.AuditLogMapper;
import com.streamforge.repository.AuditLogRepository;
import com.streamforge.repository.UserRepository;
import com.streamforge.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final AuditLogMapper auditLogMapper;

    // =========================================================
    // CREATE LOG
    // =========================================================

    @Override
    public AuditLogResponse createLog(
            Long userId,
            String action,
            String entityName,
            Long entityId
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + userId
                        )
                );

        AuditLog log = AuditLog.builder()
                .user(user)
                .action(action)
                .entityName(entityName)
                .entityId(entityId)
                .actionTime(LocalDateTime.now())
                .build();

        AuditLog savedLog =
                auditLogRepository.save(log);

        return auditLogMapper.toResponse(savedLog);
    }

    // =========================================================
    // GET USER LOGS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getUserLogs(
            Long userId
    ) {

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User not found with id: " + userId
            );
        }

        return auditLogRepository
                .findByUserUserIdOrderByActionTimeDesc(userId)
                .stream()
                .map(auditLogMapper::toResponse)
                .toList();
    }
}