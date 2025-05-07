package com.serenypals.restfulapi.model;

import java.time.LocalDate;
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
@Table(name = "booking_psikolog")
public class BookingPsikolog {

    @Id
    @Column(name = "id", length = 255, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_psikolog", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_booking_psikolog_psikolog"))
    private Psikolog idPsikolog;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_user", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_booking_psikolog_user"))
    private User idUser;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "jumlah_sesi", nullable = false)
    private int jumlahSesi;

    @Column(name = "deleted_at", nullable = true)
    private LocalDate deletedAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "edited_at", nullable = false)
    private LocalDate editedAt;
}
