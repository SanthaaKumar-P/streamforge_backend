package com.streamforge.dto.request;

import com.streamforge.enums.FileType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUploadRequest {

    private Long showId;

    private String fileName;

    private FileType fileType;

    private String fileUrl;

    private Long uploadedBy;

}