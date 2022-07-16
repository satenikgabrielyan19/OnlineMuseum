package com.onlinemuseum.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sphere",
        uniqueConstraints = @UniqueConstraint(name = "UQ_sphere_name", columnNames = "name"))
public class Sphere {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @OneToMany(mappedBy = "sphere")
    private List<Style> styles;

    @OneToMany(mappedBy = "sphere")
    private List<Artist> artists;
}
