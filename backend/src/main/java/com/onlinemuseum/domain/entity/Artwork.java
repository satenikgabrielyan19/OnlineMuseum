package com.onlinemuseum.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "artwork",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_artwork__picture_url", columnNames = "picture_url"),
                @UniqueConstraint(name = "UQ_artwork__description_url", columnNames = "description_url")})
public class Artwork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "picture_url", nullable = false)
    private String pictureUrl;

    @Column(name = "description_url", nullable = false)
    private String descriptionUrl;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false, foreignKey = @ForeignKey(name = "FK_artwork_artist"))
    private Artist artist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "style_id", nullable = false, foreignKey = @ForeignKey(name = "FK_artwork_style"))
    private Style style;

    @OneToMany(mappedBy = "artwork", fetch = FetchType.LAZY)
    private List<Comment> comments;
}
