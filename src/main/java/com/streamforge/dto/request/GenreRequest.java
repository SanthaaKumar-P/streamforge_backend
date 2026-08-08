package com.streamforge.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenreRequest {

    @NotBlank(message = "Genre name is required")
    @Size(
            min = 2,
            max = 100,
            message = "Genre name must be between 2 and 100 characters"
    )
    private String genreName;

    @Size(
            max = 500,
            message = "Genre description cannot exceed 500 characters"
    )
    private String description;
}