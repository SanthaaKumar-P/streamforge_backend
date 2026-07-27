package com.streamforge.dto.response;

import com.streamforge.enums.FileType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowFileResponse {

    private Long fileId;

    private String fileName;

    private FileType fileType;

    private String fileUrl;

    private Long showId;

}