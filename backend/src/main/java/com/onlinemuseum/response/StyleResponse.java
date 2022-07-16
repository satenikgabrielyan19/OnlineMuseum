package com.onlinemuseum.response;

import com.onlinemuseum.dto.StyleCreateDto;
import com.onlinemuseum.dto.StyleDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StyleResponse extends CustomResponse{
    private StyleDto styleDto;
}
