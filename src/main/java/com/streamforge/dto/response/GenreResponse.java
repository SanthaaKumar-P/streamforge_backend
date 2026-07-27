package com.streamforge.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenreResponse {

    private Long genreId;

    private String genreName;

    private String description;

}