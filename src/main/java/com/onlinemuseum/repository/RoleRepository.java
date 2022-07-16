package com.onlinemuseum.repository;

import com.onlinemuseum.domain.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findAppUserRoleByName(String name);
}
