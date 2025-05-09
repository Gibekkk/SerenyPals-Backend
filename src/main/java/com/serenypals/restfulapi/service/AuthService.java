package com.serenypals.restfulapi.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.serenypals.restfulapi.repository.SessionRepository;
import com.serenypals.restfulapi.util.PasswordHasherMatcher;
import com.serenypals.restfulapi.repository.LoginInfoRepository;
import com.serenypals.restfulapi.repository.UserRepository;
import com.serenypals.restfulapi.repository.UserInfoRepository;
import com.serenypals.restfulapi.dto.LoginDTO;
import com.serenypals.restfulapi.dto.UserDTO;
import com.serenypals.restfulapi.model.LoginInfo;
import com.serenypals.restfulapi.model.OTP;
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
    private PasswordHasherMatcher passwordHasherMatcher;

    private DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd");

    public String login(LoginDTO loginDTO) {
        Optional<LoginInfo> loginInfoOptional = loginInfoRepository.findByEmail(loginDTO.getEmail());
        if (loginInfoOptional.isPresent()) {
            LoginInfo loginInfo = loginInfoOptional.get();
            if (passwordHasherMatcher.matchPassword(loginDTO.getPassword(), loginInfo.getPassword())) {
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
        userInfoRepository.save(userInfo);

        return loginInfo;
    }

    public Optional<LoginInfo> findLoginInfoByIdLogin(String loginId) {
        return loginInfoRepository.findById(loginId);
    }

    public Optional<LoginInfo> findLoginInfoByEmail(String email) {
        return loginInfoRepository.findByEmail(email);
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

    @Transactional
    public Boolean logout(String token) {
        Optional<Session> sessionOptional = sessionRepository.findByToken(token);
        if (sessionOptional.isPresent()) {
            Session session = sessionOptional.get();
            sessionRepository.delete(session);
            return true;
        }
        return false;
    }

    @Transactional
    public Boolean deleteSession(String loginId) {
        Optional<LoginInfo> loginInfoOptional = findLoginInfoByIdLogin(loginId);
        if(loginInfoOptional.isPresent()) {
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