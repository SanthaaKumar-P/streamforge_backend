package com.streamforge.service;

import com.streamforge.dto.request.FileUploadRequest;
import com.streamforge.dto.response.ShowFileResponse;

import java.util.List;

public interface ShowFileService {

    ShowFileResponse uploadFile(FileUploadRequest request);

    List<ShowFileResponse> getFilesByShow(Long showId);

    void deleteFile(Long fileId);

}