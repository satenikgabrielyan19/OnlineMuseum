package com.onlinemuseum.domain.entity;

import com.onlinemuseum.domain.enumentity.*;
import lombok.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.*;

import javax.persistence.*;
import java.time.*;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_user_email", columnNames = "email"),
                @UniqueConstraint(name = "UQ_user_username", columnNames = "username")})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    @Column(name = "email", nullable = false, length = 40)
    private String email;

    @Column(name = "username", nullable = false, length = 20)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "registration_date_time", nullable = false)
    private LocalDateTime registrationDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 20)
    private UserState state=UserState.CREATED;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_user_role__user")),
            inverseJoinColumns = @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "FK_user_role__role")))
    private Set<Role> role;

    @OneToMany(mappedBy = "author")
    private List<Comment> comments;

    public Collection<? extends GrantedAuthority> getAuthorities() {

        Set<Role> roles = this.getRole();
        List<SimpleGrantedAuthority> authorities =
                new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority((role.getName())));
        }

        return authorities;
    }
}
