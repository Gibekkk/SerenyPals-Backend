package com.serenypals.restfulapi.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.serenypals.restfulapi.repository.PsikologChatRoomRepository;
import com.serenypals.restfulapi.model.PsikologChatRoom;
import com.serenypals.restfulapi.model.LoginInfo;

@Service
public class PsikologChatService {
    @Autowired
    private PsikologChatRoomRepository psikologChatRoomRepository;

    public Optional<PsikologChatRoom> findBookingById(String id) {
        return psikologChatRoomRepository.findById(id).filter(f -> f.getDeletedAt() == null);
    }

    public List<PsikologChatRoom> findAllBookingsByLoginInfo(LoginInfo loginInfo) {
        return psikologChatRoomRepository.findAll().stream()
                .filter(psikologChatRoom -> psikologChatRoom.getDeletedAt() == null)
                .filter(psikologChatRoom -> psikologChatRoom.getIdUser().getIdLogin().equals(loginInfo))
                .collect(Collectors.toList());
    }
}