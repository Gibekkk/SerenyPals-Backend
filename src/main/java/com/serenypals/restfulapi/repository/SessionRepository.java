package com.serenypals.restfulapi.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.serenypals.restfulapi.model.Session;
import com.serenypals.restfulapi.model.LoginInfo;

public interface SessionRepository extends JpaRepository<Session, String> {
    Optional<Session> findByIdLogin(LoginInfo idLogin);
    Optional<Session> findByToken(String token);
}
