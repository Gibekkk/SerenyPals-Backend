package com.serenypals.restfulapi.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.serenypals.restfulapi.repository.SessionRepository;
import com.serenypals.restfulapi.util.PasswordHasherMatcher;
import com.serenypals.restfulapi.repository.LoginInfoRepository;
import com.serenypals.restfulapi.repository.UserRepository;
import com.serenypals.restfulapi.repository.UserInfoRepository;
import com.serenypals.restfulapi.repository.SerenyPremiumUserRepository;
import com.serenypals.restfulapi.dto.LoginDTO;
import com.serenypals.restfulapi.dto.UserDTO;
import com.serenypals.restfulapi.model.LoginInfo;
import com.serenypals.restfulapi.model.SerenyPremiumUser;
import com.serenypals.restfulapi.model.Session;
import com.serenypals.restfulapi.model.User;
import com.serenypals.restfulapi.model.UserInfo;

import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private LoginInfoRepository loginInfoRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private SerenyPremiumUserRepository serenyPremiumUserRepository;

    @Autowired
    private PasswordHasherMatcher passwordHasherMatcher;

    private DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd");

    public String login(LoginDTO loginDTO) {
        Optional<LoginInfo> loginInfoOptional = findLoginInfoByEmail(loginDTO.getEmail());
        if (loginInfoOptional.isPresent()) {
            LoginInfo loginInfo = loginInfoOptional.get();
            if (passwordHasherMatcher.matchPassword(loginDTO.getPassword(), loginInfo.getPassword())) {
                Optional<Session> existingSession = findSessionByIdLogin(loginInfo);
                if (existingSession.isPresent())
                    logout(existingSession.get().getToken());
                String token = Base64.getEncoder()
                        .encodeToString(passwordHasherMatcher.hashPassword(LocalDateTime.now().toString()).getBytes());
                Session session = new Session();
                session.setToken(token);
                session.setIdLogin(loginInfo);
                session.setFcmToken(loginDTO.getFcmToken());
                session.setLastActive(LocalDateTime.now());
                session.setFirstLogin(LocalDateTime.now());
                sessionRepository.save(session);
                return token;
            }
        }
        return null;
    }

    public LoginInfo register(UserDTO userDTO) {
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setEmail(userDTO.getEmail());
        loginInfo.setPassword(passwordHasherMatcher.hashPassword(userDTO.getPassword()));
        loginInfo.setDeletedAt(null);
        loginInfo.setCreatedAt(LocalDate.now());
        loginInfo.setEditedAt(LocalDate.now());
        loginInfo.setVerifiedAt(null);
        loginInfoRepository.save(loginInfo);

        User user = new User();
        user.setNama(userDTO.getNama());
        user.setTanggalLahir(LocalDate.parse(userDTO.getTanggalLahir(), formatter));
        user.setNomorTelepon(userDTO.getNomorTelepon());
        user.setIdLogin(loginInfo);
        userRepository.save(user);

        UserInfo userInfo = new UserInfo();
        userInfo.setIdUser(user);
        userInfo.setCoins(0);
        userInfo.setDiamonds(0);
        user.setUserInfo(userInfoRepository.save(userInfo));

        SerenyPremiumUser userSerenyPremium = new SerenyPremiumUser();
        userSerenyPremium.setIdUser(user);
        userSerenyPremium.setEndAt(LocalDate.now());
        user.setUserSerenyPremium(serenyPremiumUserRepository.save(userSerenyPremium));

        return loginInfo;
    }

    public LoginInfo editUser(UserDTO userDTO, LoginInfo loginInfo) {
        loginInfo.setEditedAt(LocalDate.now());
        loginInfoRepository.save(loginInfo);

        User user = loginInfo.getIdUser();
        user.setNama(userDTO.getNama());
        user.setTanggalLahir(LocalDate.parse(userDTO.getTanggalLahir(), formatter));
        user.setNomorTelepon(userDTO.getNomorTelepon());
        userRepository.save(user);

        return loginInfo;
    }

    public Optional<Session> findSessionByIdLogin(LoginInfo loginInfo) {
        return sessionRepository.findByIdLogin(loginInfo);
    }

    public Optional<LoginInfo> findLoginInfoByIdLogin(String loginId) {
        return loginInfoRepository.findById(loginId).filter(f -> f.getDeletedAt() == null);
    }

    public Optional<LoginInfo> findLoginInfoByToken(String token) {
        Optional<Session> sessionOptional = sessionRepository.findByToken(token);
        if (sessionOptional.isPresent()) {
            return Optional.of(sessionOptional.get().getIdLogin());
        }
        return Optional.empty();
    }

    public Boolean isSessionAlive(String token) {
        return findLoginInfoByToken(token).isPresent();
    }

    public Boolean isSessionUser(String token) {
        if(isSessionAlive(token)) {
            Optional<Session> sessionOptional = sessionRepository.findByToken(token);
            if (sessionOptional.isPresent()) {
                Session session = sessionOptional.get();
                return session.getIdLogin().getIdUser() != null;
            }
        }
        return false;
    }

    public Boolean isSessionPsikolog(String token) {
        if(isSessionAlive(token)) {
            Optional<Session> sessionOptional = sessionRepository.findByToken(token);
            if (sessionOptional.isPresent()) {
                Session session = sessionOptional.get();
                return session.getIdLogin().getIdPsikolog() != null;
            }
        }
        return false;
    }

    public Optional<LoginInfo> findLoginInfoByEmail(String email) {
        return loginInfoRepository.findByEmail(email).filter(f -> f.getDeletedAt() == null);
    }

    public String verifyLoginInfo(LoginInfo loginInfo, String fcmToken) {
        loginInfo.setVerifiedAt(LocalDate.now());
        loginInfoRepository.save(loginInfo);
        String token = Base64.getEncoder()
                .encodeToString(passwordHasherMatcher.hashPassword(LocalDateTime.now().toString()).getBytes());
        Session session = new Session();
        session.setToken(token);
        session.setIdLogin(loginInfo);
        session.setFcmToken(fcmToken);
        session.setLastActive(LocalDateTime.now());
        session.setFirstLogin(LocalDateTime.now());
        sessionRepository.save(session);
        return token;
    }

    public String changeEmailOTP(LoginInfo loginInfo, String email) {
        loginInfo.setEmail(email);
        loginInfoRepository.save(loginInfo);
        return email;
    }

    @Transactional
    public Boolean logout(String token) {
        Optional<Session> sessionOptional = sessionRepository.findByToken(token);
        if (sessionOptional.isPresent()) {
            Session session = sessionOptional.get();
            LoginInfo loginInfo = session.getIdLogin();
            session.setIdLogin(null);
            loginInfo.setSession(null);
            sessionRepository.delete(session);
            loginInfoRepository.save(loginInfo);
            return true;
        }
        return false;
    }

    public Boolean emailAvailable(String email) {
        return !loginInfoRepository.existsByEmail(email);
    }

    public Boolean emailEditable(String email, LoginInfo loginInfo) {
        return !loginInfo.getEmail().equalsIgnoreCase(email) && emailAvailable(email);
    }

    @Transactional
    public Boolean deleteSession(String loginId) {
        Optional<LoginInfo> loginInfoOptional = findLoginInfoByIdLogin(loginId);
        if (loginInfoOptional.isPresent()) {
            LoginInfo loginInfo = loginInfoOptional.get();
            Optional<Session> sessionOptional = sessionRepository.findByIdLogin(loginInfo);
            if (sessionOptional.isPresent()) {
                Session session = sessionOptional.get();
                sessionRepository.delete(session);
                return true;
            }
        }
        return false;
    }
}