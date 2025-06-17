package com.serenypals.restfulapi.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serenypals.restfulapi.model.PsikologChatRoom;
import com.serenypals.restfulapi.model.Psikolog;
import com.serenypals.restfulapi.model.User;

public interface PsikologChatRoomRepository extends JpaRepository<PsikologChatRoom, String> {
    Optional<PsikologChatRoom> findByIdPsikologAndIdUser(Psikolog psikolog, User user)
}
