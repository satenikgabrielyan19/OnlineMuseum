package com.onlinemuseum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArtworkCreateDto {

    @NotNull
    private String fullName;

    @NotNull
    private String style;

    @NotNull
    private Long artistId;

}
