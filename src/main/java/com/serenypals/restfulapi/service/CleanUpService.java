package com.serenypals.restfulapi.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.serenypals.restfulapi.repository.*;

import org.springframework.transaction.annotation.Transactional;

import com.serenypals.restfulapi.model.*;

@Service
public class CleanUpService {
    
    @Autowired
    private LoginInfoRepository loginInfoRepository;
    
    @Autowired
    private AIChatRoomRepository aiChatRoomRepository;
    
    @Autowired
    private AuthService authService;

    private int deleteDays = 30;
    private LocalDate today = LocalDate.now();

    private int compareDate(LocalDate deletedAt) {
        int daysElapsed = (int) ChronoUnit.DAYS.between(deletedAt, today);
        return daysElapsed;
    }

    private boolean inDeletion(LocalDate deletedAt) {
        return deletedAt != null ? compareDate(deletedAt) >= deleteDays : false;
    }

    @Transactional
    public void cleanChatRoom() {
        for(AIChatRoom aiChatRoom : aiChatRoomRepository.findAll()){
            if(inDeletion(aiChatRoom.getDeletedAt())) {
                authService.deleteSession(aiChatRoom.getId());
                aiChatRoomRepository.delete(aiChatRoom);
            }
        }
    }

    @Transactional
    public void cleanLoginInfo() {
        for(LoginInfo loginInfo : loginInfoRepository.findAll()){
            if(inDeletion(loginInfo.getDeletedAt())) {
                authService.deleteSession(loginInfo.getId());
                loginInfoRepository.delete(loginInfo);
            }
        }
    }

    @Transactional
    public void cleanLoginInfo(LoginInfo loginInfo) {
        authService.deleteSession(loginInfo.getId());
        loginInfoRepository.delete(loginInfo);
    }

    @Transactional
    public void fullClean() {
        cleanLoginInfo();
        cleanChatRoom();
    }
}