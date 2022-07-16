package com.onlinemuseum.dto;

import lombok.*;

import javax.validation.constraints.*;
import java.time.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDeleteDto {

    @NotNull
    private LocalDateTime removalDateTime;
}
