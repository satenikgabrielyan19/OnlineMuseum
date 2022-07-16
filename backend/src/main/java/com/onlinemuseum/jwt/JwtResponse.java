package com.onlinemuseum.jwt;

import lombok.*;

@Data

@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
    private String jwtToken;
}
