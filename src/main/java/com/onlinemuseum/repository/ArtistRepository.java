package com.onlinemuseum.repository;

import com.onlinemuseum.domain.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.*;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    @Query(
            "SELECT a FROM Artist a " +
                    "WHERE (a.enabled = true )" +
                    "AND (a.id = :id)"
    )
    Optional<Artist> findById(Long id);

    @Query(
            value = "SELECT a FROM Artist a " +
                    "LEFT JOIN FETCH a.styles st " +
                    "LEFT JOIN FETCH a.sphere sp " +
                    "WHERE (:fullName IS NULL OR LOWER(a.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))) " +
                    "AND (:birthYearFrom IS NULL OR a.birthYear >= :birthYearFrom) " +
                    "AND (:birthYearTo IS NULL OR a.birthYear <= :birthYearTo) " +
                    "AND (:style IS NULL OR LOWER(st.name) LIKE LOWER(:style)) " +
                    "AND (LOWER(sp.name) LIKE LOWER(:sphere)) " +
                    "AND (a.enabled IS TRUE)",
            countQuery = "SELECT COUNT(a) FROM Artist a " +
                    "LEFT JOIN a.styles st " +
                    "LEFT JOIN a.sphere sp " +
                    "WHERE (:fullName IS NULL OR LOWER(a.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))) " +
                    "AND (:birthYearFrom IS NULL OR a.birthYear >= :birthYearFrom) " +
                    "AND (:birthYearTo IS NULL OR a.birthYear <= :birthYearTo) " +
                    "AND (:style IS NULL OR LOWER(st.name) LIKE LOWER(:style)) " +
                    "AND (LOWER(sp.name) LIKE LOWER(:sphere)) " +
                    "AND (a.enabled IS TRUE)"
    )
    Page<Artist> findByCriteria(@Param("fullName") String fullName, @Param("birthYearFrom") Integer birthYearFrom,
                                @Param("birthYearTo") Integer birthYearTo, @Param("style") String style,
                                PageRequest pageRequest, @Param("sphere") String sphere
    );

    Optional<Artist> findTopByOrderByIdDesc();
}
