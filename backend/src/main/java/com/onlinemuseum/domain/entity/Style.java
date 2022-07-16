package com.onlinemuseum.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "style")
public class Style {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description_url", nullable = false, length = 1000)
    private String descriptionUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sphere_id", nullable = false, foreignKey = @ForeignKey(name = "FK_style_sphere"))
    private Sphere sphere;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @OneToMany(mappedBy = "style")
    private List<Artwork> artworks;
}
