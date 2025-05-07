package com.serenypals.restfulapi.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

import com.serenypals.restfulapi.enums.Emoji;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "virtual_diary")
public class VirtualDiary {

    @Id
    @Column(name = "id", length = 255, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_user", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_virtual_diary_user"))
    private User idUser;

    @Column(name = "judul", length = 50, nullable = false)
    private String judul;

    @Column(name = "content", nullable = false, columnDefinition="TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "emoji", nullable = false)
    private Emoji emoji;

    @Column(name = "deleted_at", nullable = true)
    private LocalDate deletedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "edited_at", nullable = false)
    private LocalDate editedAt;
}
