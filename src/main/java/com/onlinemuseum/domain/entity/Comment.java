package com.onlinemuseum.domain.entity;

import com.onlinemuseum.domain.enumentity.*;
import lombok.*;

import javax.persistence.*;
import java.time.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "creation_date_time", nullable = false)
    private LocalDateTime creationDateTime;

    @Column(name = "removal_date_time", nullable = false)
    private LocalDateTime removalDateTime;

    @Column(name = "text", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 20)
    private CommentState state;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "FK_comment_comment_parent"))
    private Comment parent;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_version_id", foreignKey = @ForeignKey(name = "FK_comment_comment_previous_version"))
    private Comment previousVersion;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "FK_comment_user"))
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artwork_id", nullable = false, foreignKey = @ForeignKey(name = "FK_comment_artwork"))
    private Artwork artwork;


}
