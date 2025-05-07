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
@Table(name = "pembelian_serenyshop")
public class PembelianSerenyShop {

    @Id
    @Column(name = "id", length = 255, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_serenyshop", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_pembelian_serenyshop_serenyshop"))
    private SerenyShop idSerenyShop;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_user", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_pembelian_serenyshop_user"))
    private User idUser;

    @Column(name = "harga_akhir", nullable = false)
    private int hargaAkhir;

    @Column(name = "diskon_persen", nullable = false)
    private int diskonPersen;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;
}
