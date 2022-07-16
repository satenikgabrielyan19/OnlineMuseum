package com.onlinemuseum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArtistCreateDto {

    @NotNull
    private String fullName;

    @NotNull
    private String sphere;

    @NotNull
    private LocalDate birthYear;

    private LocalDate deathYear;

}
