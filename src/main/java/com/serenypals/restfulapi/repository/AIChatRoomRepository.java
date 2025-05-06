package com.serenypals.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serenypals.restfulapi.model.AIChatRoom;

public interface AIChatRoomRepository extends JpaRepository<AIChatRoom, String> {
}
