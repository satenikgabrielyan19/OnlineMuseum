package com.onlinemuseum.repository;

import com.onlinemuseum.domain.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.*;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtworkRepository extends JpaRepository<Artwork, Long> {
    @Query(
            "SELECT a FROM Artwork a " +
                    "WHERE (a.id = :id)" +
                    "AND (a.enabled = true)"
    )
    Optional<Artwork> findById(Long id);

    Optional<Artwork> findTopByOrderByIdDesc();

    @Query(
            "SELECT a FROM Artwork a " +
                    "WHERE (a.artist.id = :artistId) " +
                    "AND (a.enabled = true)"
    )
    Page<Artwork> findAllByArtist(@Param("artistId") Long artistId, PageRequest pageRequest);

    @Query(
            "SELECT a FROM Artwork a " +
                    "WHERE (a.style.id = :styleId) " +
                    "AND (a.enabled = true)"
    )
    Page<Artwork> findAllByStyle(@Param("styleId") Long styleId, PageRequest pageRequest);
}
