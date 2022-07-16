package com.onlinemuseum.dto;

import lombok.*;
import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SphereUpdateDto {
    @NotNull
    private String name;
}
