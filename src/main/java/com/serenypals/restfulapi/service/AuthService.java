package com.serenypals.restfulapi.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.serenypals.restfulapi.repository.SessionRepository;
import com.serenypals.restfulapi.util.PasswordHasherMatcher;
import com.serenypals.restfulapi.repository.LoginInfoRepository;
import com.serenypals.restfulapi.dto.LoginDTO;
import com.serenypals.restfulapi.model.LoginInfo;
import com.serenypals.restfulapi.model.Session;

import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    
    @Autowired
    private LoginInfoRepository loginInfoRepository;
    
    @Autowired
    private SessionRepository sessionRepository;
    
    @Autowired
    private PasswordHasherMatcher passwordHasherMatcher;

    public String login(LoginDTO loginDTO) {
        Optional<LoginInfo> loginInfoOptional = loginInfoRepository.findByEmail(loginDTO.getEmail());
        if(loginInfoOptional.isPresent()) {
            LoginInfo loginInfo = loginInfoOptional.get();
            if (passwordHasherMatcher.matchPassword(loginDTO.getPassword(), loginInfo.getPassword())) {
                String token = Base64.getEncoder().encodeToString(passwordHasherMatcher.hashPassword(LocalDateTime.now().toString()).getBytes());
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
}