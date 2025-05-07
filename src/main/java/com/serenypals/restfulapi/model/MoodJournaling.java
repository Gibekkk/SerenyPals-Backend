package com.serenypals.restfulapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "mood_journaling")
public class MoodJournaling {

    @Id
    @Column(name = "id", length = 255, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_user", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_mood_journaling_user"))
    private User idUser;

    @Column(name = "gangguan", length = 255, nullable = false)
    private String gangguan;

    @Column(name = "mood_scale", nullable = false)
    private int moodScale;

    @Column(name = "cerita", nullable = false, columnDefinition="TEXT")
    private String cerita;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;
}
