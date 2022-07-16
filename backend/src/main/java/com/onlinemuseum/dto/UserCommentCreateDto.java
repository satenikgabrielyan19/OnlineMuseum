package com.onlinemuseum.dto;

import lombok.*;

import javax.validation.constraints.*;
import java.time.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCommentCreateDto {

    @NotNull
    private String username;

    @NotNull
    private LocalDateTime creationDateTime;

    @NotNull
    private String content;
}
