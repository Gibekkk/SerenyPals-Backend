package com.serenypals.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serenypals.restfulapi.model.PsikologChat;

public interface PsikologChatRepository extends JpaRepository<PsikologChat, String> {
}
