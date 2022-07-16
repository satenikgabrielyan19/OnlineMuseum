package com.onlinemuseum.repository;

import com.onlinemuseum.domain.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface SphereRepository extends JpaRepository<Sphere, Long> {
    boolean existsByName(String name);

    Optional<Sphere> getByName(String name);

    List<Sphere> findAllByEnabledIsTrue();
}
