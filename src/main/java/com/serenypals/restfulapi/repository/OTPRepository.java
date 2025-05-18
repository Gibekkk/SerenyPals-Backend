package com.serenypals.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import com.serenypals.restfulapi.model.OTP;
import com.serenypals.restfulapi.model.LoginInfo;

public interface OTPRepository extends JpaRepository<OTP, String> {
    Optional<OTP> findByIdLogin(LoginInfo loginInfo);
    Boolean existsByFcmTokenEmail(String fcmTokenEmail);
}
