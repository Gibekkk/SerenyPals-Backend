package com.serenypals.restfulapi.model;

import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "serenyshop")
public class SerenyShop {

    @Id
    @Column(name = "id", length = 255, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "jumlah_diamond", nullable = false)
    private int jumlahDiamond;

    @Column(name = "harga", nullable = false)
    private int harga;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "edited_at", nullable = false)
    private LocalDate editedAt;

    @Column(name = "deleted_at", nullable = true)
    private LocalDate deletedAt;
    
    @OneToMany(mappedBy = "idSerenyShop", cascade = CascadeType.ALL)
    private Set<PembelianSerenyShop> serenyShopPurchases;
}
