package com.serenypals.restfulapi.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "psikolog_chat_room")
public class PsikologChatRoom {

    @Id
    @Column(name = "id", length = 255, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_user", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_psikolog_chat_room_user"))
    private User idUser;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_psikolog", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_psikolog_chat_room_psikolog"))
    private Psikolog idPsikolog;

    @Column(name = "deleted_at", nullable = true)
    private LocalDate deletedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "edited_at", nullable = false)
    private LocalDateTime editedAt;

    @OneToMany(mappedBy = "idChatRoom", cascade = CascadeType.ALL)
    private Set<PsikologChat> chats;

    public LocalDateTime getLastChatDateTime() {
        return chats.size() > 0 ? chats.stream()
                .map(PsikologChat::getCreatedAt)
                .max(LocalDateTime::compareTo).get() : this.createdAt;
    }

    public PsikologChat getLastChat() {
        List<PsikologChat> psikologChatList = chats.stream()
                .sorted(Comparator.comparing(PsikologChat::getCreatedAt))
                .collect(Collectors.toList());
        return psikologChatList.size() > 0 ? psikologChatList.get(psikologChatList.size() - 1) : null;
    }
}
