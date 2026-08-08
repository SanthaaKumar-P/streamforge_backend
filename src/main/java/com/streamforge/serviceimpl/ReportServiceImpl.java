package com.streamforge.serviceimpl;

import com.streamforge.dto.response.ReportResponse;
import com.streamforge.entity.Report;
import com.streamforge.entity.User;
import com.streamforge.exception.ResourceNotFoundException;
import com.streamforge.mapper.ReportMapper;
import com.streamforge.repository.ReportRepository;
import com.streamforge.repository.UserRepository;
import com.streamforge.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ReportMapper reportMapper;

    @Override
    public ReportResponse generateReport(
            Long userId,
            String reportName,
            String reportType
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + userId
                        )
                );

        Report report = Report.builder()
                .generatedBy(user)
                .reportName(reportName)
                .reportType(reportType)
                .build();

        return reportMapper.toResponse(
                reportRepository.save(report)
        );
    }

    @Override
    public List<ReportResponse> getReportsByUser(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User not found with id: " + userId
            );
        }

        return reportRepository
                .findByGeneratedByUserId(userId)
                .stream()
                .map(reportMapper::toResponse)
                .toList();
    }
}