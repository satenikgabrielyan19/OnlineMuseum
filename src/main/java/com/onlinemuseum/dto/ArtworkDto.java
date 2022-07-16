package com.onlinemuseum.dto;

import com.onlinemuseum.domain.entity.Artist;
import com.onlinemuseum.domain.entity.Style;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArtworkDto {

    private String fullName;

    private String pictureUrl;

    private String descriptionUrl;

    private StyleToShowInArtworkDto style;

    private ArtistToShowInArtworkDto artist;

}
