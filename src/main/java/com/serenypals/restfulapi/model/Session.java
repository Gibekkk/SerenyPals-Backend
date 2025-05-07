package com.serenypals.restfulapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "session")
public class Session {
    @Id
    @Column(name = "token", nullable = false)
    private String token;

    @OneToOne
    @JoinColumn(nullable = false, name = "id_login", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_session_login"))
    private LoginInfo idLogin;

    @Column(name = "fcm_token", length = 255, nullable = false)
    private String fcmToken;

    @Column(name = "last_active", nullable = false)
    private LocalDateTime lastActive;

    @Column(name = "first_login", nullable = false)
    private LocalDateTime firstLogin;

}
