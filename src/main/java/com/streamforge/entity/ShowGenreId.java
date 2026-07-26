package com.streamforge.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ShowGenreId implements Serializable {

    private Long show;

    private Long genre;

}