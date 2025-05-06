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
import jakarta.persistence.ManyToOne;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sereny_premium_user")
public class SerenyPremiumUser {

    @Id
    @Column(name = "id", length = 255, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(nullable = false, name = "id_user", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_sereny_premium_user_user"))
    private User idUser;

    @ManyToOne
    @JoinColumn(nullable = true, name = "id_serenypreium", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_sereny_premium_user_serenypremium"))
    private SerenyPremium idSerenyPremium;

    @Column(name = "end_at", nullable = false)
    private LocalDate endAt;

    @Column(name = "last_purchase", nullable = true)
    private LocalDate lastPurchase;
}
