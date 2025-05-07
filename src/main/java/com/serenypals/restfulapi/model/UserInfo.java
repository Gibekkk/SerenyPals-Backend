package com.serenypals.restfulapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.OneToOne;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_info")
public class UserInfo {

    @Id
    @Column(name = "id", length = 255, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(nullable = false, name = "id_user", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_user_info_user"))
    private User idUser;

    @Column(name = "coins", nullable = false)
    private int coins;

    @Column(name = "diamonds", nullable = false)
    private int diamonds;
}
