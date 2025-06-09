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
    private PsikologChatRoomRepository psikologChatRoomRepository;
    
    @Autowired
    private BookingPsikologRepository bookingPsikologRepository;
    
    @Autowired
    private SharingForumRepository sharingForumRepository;
    
    @Autowired
    private SharingForumCommentsRepository sharingForumCommentsRepository;
    
    @Autowired
    private VirtualDiaryRepository virtualDiaryRepository;
    
    @Autowired
    private TipsRepository tipsRepository;
    
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
    public void cleanForum() {
        for(SharingForum sharingForum : sharingForumRepository.findAll()){
            if(inDeletion(sharingForum.getDeletedAt())) {
                sharingForumRepository.delete(sharingForum);
            }
        }
    }

    @Transactional
    public void cleanDiaries() {
        for(VirtualDiary virtualDiary : virtualDiaryRepository.findAll()){
            if(inDeletion(virtualDiary.getDeletedAt())) {
                virtualDiaryRepository.delete(virtualDiary);
            }
        }
    }

    @Transactional
    public void cleanTips() {
        for(Tips tips : tipsRepository.findAll()){
            if(inDeletion(tips.getDeletedAt())) {
                tipsRepository.delete(tips);
            }
        }
    }

    @Transactional
    public void cleanForumComments() {
        for(SharingForumComments sharingForumComments : sharingForumCommentsRepository.findAll()){
            if(inDeletion(sharingForumComments.getDeletedAt())) {
                sharingForumCommentsRepository.delete(sharingForumComments);
            }
        }
    }

    @Transactional
    public void cleanBooking() {
        for(BookingPsikolog bookingPsikolog : bookingPsikologRepository.findAll()){
            if(inDeletion(bookingPsikolog.getDeletedAt())) {
                bookingPsikologRepository.delete(bookingPsikolog);
            }
        }
    }

    @Transactional
    public void cleanPsikologChatRoom() {
        for(PsikologChatRoom psikologChatRoom : psikologChatRoomRepository.findAll()){
            if(inDeletion(psikologChatRoom.getDeletedAt())) {
                psikologChatRoomRepository.delete(psikologChatRoom);
            }
        }
    }

    @Transactional
    public void cleanAIChatRoom() {
        for(AIChatRoom aiChatRoom : aiChatRoomRepository.findAll()){
            if(inDeletion(aiChatRoom.getDeletedAt())) {
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
        cleanAIChatRoom();
        cleanPsikologChatRoom();
        cleanBooking();
        cleanForum();
    }
}