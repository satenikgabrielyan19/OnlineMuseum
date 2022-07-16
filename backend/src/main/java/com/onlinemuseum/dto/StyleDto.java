package com.onlinemuseum.dto;

import com.onlinemuseum.domain.entity.Sphere;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StyleDto {
    private Long id;

    private String name;

    private String descriptionUrl;
    @NotNull
    private String sphere;


}
