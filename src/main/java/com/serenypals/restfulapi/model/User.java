package com.serenypals.restfulapi.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.OneToOne;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @Column(name = "id", length = 255, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(nullable = false, name = "id_login", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_user_login"))
    private LoginInfo idLogin;

    @Column(name = "nama", length = 50, nullable = false)
    private String nama;

    @Column(name = "nomor_telepon", length = 255, nullable = false)
    private String nomorTelepon;
    
    @Column(name = "tanggal_lahir", nullable = false)
    private LocalDate tanggalLahir;

    @OneToOne(mappedBy = "idUser", cascade = CascadeType.ALL)
    private UserInfo userInfo;

    @OneToOne(mappedBy = "idUser", cascade = CascadeType.ALL)
    private SerenyPremiumUser userSerenyPremium;

    @OneToMany(mappedBy = "idUser", cascade = CascadeType.ALL)
    private Set<UserTask> userTasks;

    @OneToMany(mappedBy = "idUser", cascade = CascadeType.ALL)
    private Set<AIChatRoom> aiChatRooms;
    
    @OneToMany(mappedBy = "idUser", cascade = CascadeType.ALL)
    private Set<PsikologChatRoom> chatRooms;

    @OneToMany(mappedBy = "idUser", cascade = CascadeType.ALL)
    private Set<BookingPsikolog> userBookings;

    @OneToMany(mappedBy = "idUser", cascade = CascadeType.ALL)
    private Set<CheckIn> userCheckIns;

    @OneToMany(mappedBy = "idUser", cascade = CascadeType.ALL)
    private Set<FavoriteTips> userFavoriteTips;

    @OneToMany(mappedBy = "idUser", cascade = CascadeType.ALL)
    private Set<MoodJournaling> userJournals;

    @OneToMany(mappedBy = "idUser", cascade = CascadeType.ALL)
    private Set<VirtualDiary> userDiaries;

    @OneToMany(mappedBy = "idUser", cascade = CascadeType.ALL)
    private Set<SharingForum> userForums;

    @OneToMany(mappedBy = "idUser", cascade = CascadeType.ALL)
    private Set<SharingForumComments> userForumComments;

    @OneToMany(mappedBy = "idUser", cascade = CascadeType.ALL)
    private Set<SharingForumLikes> userForumLikes;
}
