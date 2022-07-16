package com.onlinemuseum.repository;

import com.onlinemuseum.domain.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.Optional;

@Repository
public interface StyleRepository extends JpaRepository<Style, Long> {

    boolean existsStyleByName(String name);

    Optional<Style> findByName(String name);

    @Query(
            "SELECT a FROM Style a " +
                    "WHERE (a.sphere.id =:id)" +
                    "AND (a.enabled = true ) "

    )
    Page<Style> findBySphere(PageRequest pageRequest,Long id);

    @Query(
            "SELECT a from Artist a " +
                    "inner join a.artworks art where art.style.id = :styleId"
    )
    Page<Artist> artistsOfStyle(PageRequest pageRequest, Long styleId);


}
