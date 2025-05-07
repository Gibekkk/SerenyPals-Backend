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
@Table(name = "psikolog")
public class Psikolog {

    @Id
    @Column(name = "id", length = 255, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(nullable = false, name = "id_login", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_psikolog_login"))
    private LoginInfo idLogin;

    @Column(name = "nama", length = 50, nullable = false)
    private String nama;

    @Column(name = "nomor_telepon", length = 255, nullable = false)
    private String nomorTelepon;
}
