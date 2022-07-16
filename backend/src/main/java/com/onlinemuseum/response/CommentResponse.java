package com.onlinemuseum.response;

import com.onlinemuseum.dto.*;
import lombok.*;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse extends CustomResponse {
    private List<CommentDto> comments;
}
