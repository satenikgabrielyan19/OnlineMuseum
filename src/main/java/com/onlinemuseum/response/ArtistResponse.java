package com.onlinemuseum.response;

import com.onlinemuseum.dto.ArtistDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArtistResponse extends CustomResponse {
    private ArtistDto artistDto;
}
