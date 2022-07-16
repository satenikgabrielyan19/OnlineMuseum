package com.onlinemuseum.domain.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "artist",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_artist__photo_url", columnNames = "photo_url"),
                @UniqueConstraint(name = "UQ_artist__bio_url", columnNames = "bio_url")})
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @Column(name = "birth_year")
    private LocalDate birthYear;

    @Column(name = "death_year")
    private LocalDate deathYear;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "bio_url")
    private String bioUrl;

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL)
    private List<Artwork> artworks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sphere_id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_artist_sphere"))
    private Sphere sphere;

    @ManyToMany
    @JoinTable(name = "artist_style",
            joinColumns = @JoinColumn(name = "artist_id", referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "FK_artist_style__artist")),
            inverseJoinColumns = @JoinColumn(name = "style_id", referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "FK_artist_style__style")))
    private Set<Style> styles;
}
