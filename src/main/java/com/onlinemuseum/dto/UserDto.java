package com.onlinemuseum.dto;

import com.onlinemuseum.domain.entity.Role;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    private String firstName;

    private String lastname;

    private String username;

    private String password;

    private LocalDateTime registrationDateTime;

    private LocalDate birthday;

    private Set<Role> role;


    @NotNull
    private String email;

    public UserDto(String firstName, String lastname,
                   String username, String password,
                   LocalDateTime registrationDateTime, LocalDate birthday,
                   Set<Role> role, String email) {
        this.firstName = firstName;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.registrationDateTime = LocalDateTime.now();
        this.birthday = birthday;
        this.role = role;
        this.email = email;
    }


}
