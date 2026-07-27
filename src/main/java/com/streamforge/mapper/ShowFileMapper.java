package com.streamforge.mapper;

import com.streamforge.dto.response.ShowFileResponse;
import com.streamforge.entity.ShowFile;
import org.springframework.stereotype.Component;

@Component
public class ShowFileMapper {

    public ShowFileResponse toResponse(ShowFile file){

        return ShowFileResponse.builder()
                .fileId(file.getFileId())
                .fileName(file.getFileName())
                .fileType(file.getFileType())
                .fileUrl(file.getFileUrl())
                .showId(file.getShow().getShowId())
                .build();

    }

}