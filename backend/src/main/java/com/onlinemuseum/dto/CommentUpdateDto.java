package com.onlinemuseum.dto;

import lombok.*;

import javax.validation.constraints.*;
import java.time.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdateDto {

    @NotNull
    private LocalDateTime creationDateTime;

    @NotNull
    private String content;
}
