package com.streamforge.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="show_genres")
@IdClass(ShowGenreId.class)
public class ShowGenre {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="show_id")
    @JsonBackReference
    private Show show;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="genre_id")
    @JsonBackReference
    private Genre genre;

}