package com.onlinemuseum.response;

import com.onlinemuseum.dto.ArtworkDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArtworkResponse extends CustomResponse{
    private ArtworkDto artworkDto;
}
