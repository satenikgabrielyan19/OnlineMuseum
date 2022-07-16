package com.onlinemuseum.registration.token;

import com.onlinemuseum.domain.entity.User;
import com.onlinemuseum.dto.UserDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tokens")
public class ConfirmationToken {
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String token;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime expiredAt;

    private LocalDateTime confirmedAt;
    @ManyToOne
    @JoinColumn(
            nullable = false,
            name = "app_user_id"
    )
    private User appUser;

    public ConfirmationToken(String token,
                             LocalDateTime createdAt, LocalDateTime expiredAt,

                             User appUser) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;

        this.appUser = appUser;
    }

}
