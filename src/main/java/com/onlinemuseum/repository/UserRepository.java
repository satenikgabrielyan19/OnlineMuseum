package com.onlinemuseum.repository;

import com.onlinemuseum.domain.entity.*;
import com.onlinemuseum.domain.enumentity.UserState;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE User a " +
            "SET a.state = " + "'ACTIVATED'" + " WHERE a.email = :email")
    int enableAppUser(String email);

    User findByResetPasswordToken(String token);
}
