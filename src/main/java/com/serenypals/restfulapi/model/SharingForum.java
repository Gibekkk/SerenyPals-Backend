package com.serenypals.restfulapi.model;

import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sharing_forum")
public class SharingForum {

    @Id
    @Column(name = "id", length = 255, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_user", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_sharing_forum_user"))
    private User idUser;

    @Column(name = "judul", length = 255, nullable = false)
    private String judul;

    @Column(name = "content", nullable = false, columnDefinition="TEXT")
    private String content;

    @Column(name = "deleted_at", nullable = true)
    private LocalDate deletedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "edited_at", nullable = false)
    private LocalDate editedAt;
    
    @OneToMany(mappedBy = "idForum", cascade = CascadeType.ALL)
    private Set<SharingForumComments> sharingForumComments;
    
    @OneToMany(mappedBy = "idForum", cascade = CascadeType.ALL)
    private Set<SharingForumLikes> sharingForumLikes;
}
