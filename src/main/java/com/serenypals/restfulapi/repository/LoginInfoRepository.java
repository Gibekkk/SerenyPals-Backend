package com.serenypals.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import com.serenypals.restfulapi.model.LoginInfo;

public interface LoginInfoRepository extends JpaRepository<LoginInfo, String> {
    boolean existsByEmail(String email);
    Optional<LoginInfo> findByEmail(String email);
}
