package com.onlinemuseum.dto;

import lombok.*;

import java.time.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long id;

    private LocalDateTime creationDateTime;

    private String content;

    private String authorUsername;
}
