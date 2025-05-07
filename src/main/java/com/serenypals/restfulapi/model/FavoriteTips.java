package com.serenypals.restfulapi.model;

import java.time.LocalDate;

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
@Table(name = "favorite_tips")
public class FavoriteTips {

    @Id
    @Column(name = "id", length = 255, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_user", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_favorite_tips_user"))
    private User idUser;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_tips", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_favorite_tips_tips"))
    private Tips idTips;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;
}
