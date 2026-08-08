package com.streamforge.dto.request;

import com.streamforge.enums.FileType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUploadRequest {

    @NotNull(message = "Show ID is required")
    @Positive(message = "Show ID must be positive")
    private Long showId;

    @NotBlank(message = "File name is required")
    @Size(
            max = 255,
            message = "File name cannot exceed 255 characters"
    )
    private String fileName;

    @NotNull(message = "File type is required")
    private FileType fileType;

    @NotBlank(message = "File URL is required")
    @Size(
            max = 1000,
            message = "File URL cannot exceed 1000 characters"
    )
    private String fileUrl;

    @NotNull(message = "Uploaded by user ID is required")
    @Positive(message = "Uploaded by user ID must be positive")
    private Long uploadedBy;
}