package com.onlinemuseum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArtistDto {
    private Long id;

    private String fullName;

    private String photoUrl;

    private String bioUrl;

    private LocalDate birthYear;

    private LocalDate deathYear;

}
