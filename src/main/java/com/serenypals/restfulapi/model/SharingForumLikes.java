package com.serenypals.restfulapi.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sharing_forum_likes")
public class SharingForumLikes {

    @Id
    @Column(name = "id", length = 255, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_user", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_sharing_forum_likes_user"))
    private User idUser;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_forum", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_sharing_forum_likes_forum"))
    private SharingForum idForum;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
