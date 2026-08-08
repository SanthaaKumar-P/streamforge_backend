package com.streamforge.serviceimpl;

import com.streamforge.dto.request.FileUploadRequest;
import com.streamforge.dto.response.ShowFileResponse;
import com.streamforge.entity.Show;
import com.streamforge.entity.ShowFile;
import com.streamforge.entity.User;
import com.streamforge.exception.ResourceNotFoundException;
import com.streamforge.mapper.ShowFileMapper;
import com.streamforge.repository.ShowFileRepository;
import com.streamforge.repository.ShowRepository;
import com.streamforge.repository.UserRepository;
import com.streamforge.service.ShowFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowFileServiceImpl implements ShowFileService {

    private final ShowFileRepository showFileRepository;
    private final ShowRepository showRepository;
    private final UserRepository userRepository;
    private final ShowFileMapper showFileMapper;

    @Override
    public ShowFileResponse uploadFile(
            FileUploadRequest request
    ) {

        Show show = showRepository.findById(request.getShowId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Show not found with id: "
                                        + request.getShowId()
                        )
                );

        User user = userRepository.findById(request.getUploadedBy())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: "
                                        + request.getUploadedBy()
                        )
                );

        ShowFile file = ShowFile.builder()
                .show(show)
                .uploadedBy(user)
                .fileName(request.getFileName())
                .fileType(request.getFileType())
                .fileUrl(request.getFileUrl())
                .build();

        return showFileMapper.toResponse(
                showFileRepository.save(file)
        );
    }

    @Override
    public List<ShowFileResponse> getFilesByShow(
            Long showId
    ) {

        if (!showRepository.existsById(showId)) {
            throw new ResourceNotFoundException(
                    "Show not found with id: " + showId
            );
        }

        return showFileRepository.findByShowShowId(showId)
                .stream()
                .map(showFileMapper::toResponse)
                .toList();
    }

    @Override
    public void deleteFile(Long fileId) {

        ShowFile file = showFileRepository.findById(fileId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Show file not found with id: "
                                        + fileId
                        )
                );

        showFileRepository.delete(file);
    }
}