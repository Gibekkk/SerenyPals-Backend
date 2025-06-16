package com.serenypals.restfulapi.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

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
    private SharingForumLikesRepository sharingForumLikesRepository;

    @Autowired
    private VirtualDiaryRepository virtualDiaryRepository;

    @Autowired
    private TipsRepository tipsRepository;

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PsikologRepository psikologRepository;

    @Autowired
    private AuthService authService;

    private int deleteDays = 30;
    private LocalDate today = LocalDate.now();
    private int OTP_DELETION = 30;

    private int compareDate(LocalDate deletedAt) {
        int daysElapsed = (int) ChronoUnit.DAYS.between(deletedAt, today);
        return daysElapsed;
    }

    private boolean inDeletion(LocalDate deletedAt) {
        return deletedAt != null ? compareDate(deletedAt) >= deleteDays : false;
    }

    @Transactional
    public void cleanForum() {
        for (SharingForum sharingForum : sharingForumRepository.findAll()) {
            if (inDeletion(sharingForum.getDeletedAt())) {
                User user = sharingForum.getIdUser();
                sharingForum.setIdUser(null);
                user.getUserForums().remove(sharingForum);
                sharingForumRepository.delete(sharingForum);
                userRepository.save(user);
            }
        }
    }

    @Transactional
    public void cleanDiaries() {
        for (VirtualDiary virtualDiary : virtualDiaryRepository.findAll()) {
            if (inDeletion(virtualDiary.getDeletedAt())) {
                User user = virtualDiary.getIdUser();
                user.getUserDiaries().remove(virtualDiary);
                virtualDiaryRepository.delete(virtualDiary);
                userRepository.save(user);
            }
        }
    }

    @Transactional
    public void cleanForumLikes(SharingForumLikes sharingForumLikes) {
        SharingForum sharingForum = sharingForumLikes.getIdForum();
        sharingForumLikes.setIdForum(null);
        User user = sharingForumLikes.getIdUser();
        sharingForumLikes.setIdUser(null);
        sharingForum.getSharingForumLikes().remove(sharingForumLikes);
        user.getUserForumLikes().remove(sharingForumLikes);
        sharingForumLikesRepository.delete(sharingForumLikes);
        sharingForumRepository.save(sharingForum);
        userRepository.save(user);
    }

    @Transactional
    public void cleanForumComments() {
        for (SharingForumComments sharingForumComments : sharingForumCommentsRepository.findAll()) {
            if (inDeletion(sharingForumComments.getDeletedAt())) {
                SharingForum sharingForum = sharingForumComments.getIdForum();
                sharingForumComments.setIdForum(null);
                User user = sharingForumComments.getIdUser();
                sharingForumComments.setIdUser(null);
                sharingForum.getSharingForumComments().remove(sharingForumComments);
                user.getUserForumComments().remove(sharingForumComments);
                sharingForumCommentsRepository.delete(sharingForumComments);
                sharingForumRepository.save(sharingForum);
                userRepository.save(user);
            }
        }
    }

    @Transactional
    public void cleanBooking() {
        for (BookingPsikolog bookingPsikolog : bookingPsikologRepository.findAll()) {
            if (inDeletion(bookingPsikolog.getDeletedAt())) {
                User user = bookingPsikolog.getIdUser();
                bookingPsikolog.setIdUser(null);
                Psikolog psikolog = bookingPsikolog.getIdPsikolog();
                bookingPsikolog.setIdPsikolog(null);
                user.getUserBookings().remove(bookingPsikolog);
                psikolog.getBookings().remove(bookingPsikolog);
                bookingPsikologRepository.delete(bookingPsikolog);
                userRepository.save(user);
                psikologRepository.save(psikolog);
            }
        }
    }

    @Transactional
    public void cleanPsikologChatRoom() {
        for (PsikologChatRoom psikologChatRoom : psikologChatRoomRepository.findAll()) {
            if (inDeletion(psikologChatRoom.getDeletedAt())) {
                User user = psikologChatRoom.getIdUser();
                psikologChatRoom.setIdUser(null);
                Psikolog psikolog = psikologChatRoom.getIdPsikolog();
                psikologChatRoom.setIdPsikolog(null);
                user.getChatRooms().remove(psikologChatRoom);
                psikolog.getChatRooms().remove(psikologChatRoom);
                psikologChatRoomRepository.delete(psikologChatRoom);
                userRepository.save(user);
                psikologRepository.save(psikolog);
            }
        }
    }

    @Transactional
    public void cleanAIChatRoom() {
        for (AIChatRoom aiChatRoom : aiChatRoomRepository.findAll()) {
            if (inDeletion(aiChatRoom.getDeletedAt())) {
                User user = aiChatRoom.getIdUser();
                aiChatRoom.setIdUser(null);
                user.getAiChatRooms().remove(aiChatRoom);
                aiChatRoomRepository.delete(aiChatRoom);
                userRepository.save(user);
            }
        }
    }

    @Transactional
    public void cleanLoginInfo() {
        for (LoginInfo loginInfo : loginInfoRepository.findAll()) {
            if (inDeletion(loginInfo.getDeletedAt())) {
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
    public void cleanOTP(OTP otp) {
        boolean isRegistration = otp.getIsRegistration();
        LoginInfo loginInfo = otp.getIdLogin();
        loginInfo.setOtp(null);
        otp.setIdLogin(null);
        otpRepository.delete(otp);
        loginInfoRepository.save(loginInfo);
        if (isRegistration && loginInfo.getVerifiedAt() == null) {
            cleanLoginInfo(loginInfo);
        }
    }

    @Transactional
    public void cleanOTP() {
        List<OTP> otps = otpRepository.findAll();
        for (OTP otp : otps) {
            if (otp.getExpiryTime().plusMinutes(OTP_DELETION).isBefore(LocalDateTime.now())) {
                boolean isRegistration = otp.getIsRegistration();
                LoginInfo loginInfo = otp.getIdLogin();
                loginInfo.setOtp(null);
                otp.setIdLogin(null);
                otpRepository.delete(otp);
                loginInfoRepository.save(loginInfo);
                if (isRegistration && loginInfo.getVerifiedAt() == null) {
                    cleanLoginInfo(loginInfo);
                }
            }
        }
    }

    @Transactional
    public void fullClean() {
        cleanLoginInfo();
        cleanAIChatRoom();
        cleanPsikologChatRoom();
        cleanBooking();
        cleanForum();
        cleanOTP();
    }
}