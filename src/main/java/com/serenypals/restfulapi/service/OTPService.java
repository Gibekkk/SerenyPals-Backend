package com.serenypals.restfulapi.service;

import java.util.Optional;
import java.util.Random;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.serenypals.restfulapi.model.OTP;
import com.serenypals.restfulapi.model.LoginInfo;
import com.serenypals.restfulapi.repository.OTPRepository;

@Service
public class OTPService {

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private CleanUpService cleanUpService;

    private int OTP_LENGTH = 6;
    private int OTP_TIME_OUT = 3;

    // Generate dan simpan OTP
    public OTP generateOTP(LoginInfo loginInfo, boolean isRegistration, String fcmTokenEmail) {
        clearExistingOTP(loginInfo);
        String code = String.format("%0" + OTP_LENGTH + "d", new Random().nextInt(999_999));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(OTP_TIME_OUT);
        Optional<OTP> optionalOTP = findOTPByLoginInfo(loginInfo);
        OTP otp = optionalOTP.isPresent() ? optionalOTP.get() : new OTP();
        otp.setCode(code);
        otp.setExpiryTime(expiry);
        otp.setIsRegistration(isRegistration);
        otp.setIdLogin(loginInfo);
        otp.setFcmTokenEmail(fcmTokenEmail);
        otpRepository.save(otp);
        return otp;
    }

    public Boolean emailAvailable(String email) {
        return !otpRepository.existsByFcmTokenEmail(email);
    }

    // Generate dan simpan OTP
    public Optional<OTP> refreshOTP(LoginInfo loginInfo) {
        String code = String.format("%0" + OTP_LENGTH + "d", new Random().nextInt(999_999));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(OTP_TIME_OUT);
        Optional<OTP> existingOtp = otpRepository.findByIdLogin(loginInfo);
        if (existingOtp.isPresent()) {
            OTP otp = existingOtp.get();
            otp.setExpiryTime(expiry);
            otp.setCode(code);
            otpRepository.save(otp);
            return Optional.of(otp);
        }
        return Optional.empty();
    }

    public Optional<OTP> findOTPByLoginInfo(LoginInfo loginInfo) {
        return otpRepository.findByIdLogin(loginInfo);
    }

    public void deleteOTP(OTP otp) {
        cleanUpService.cleanOTP(otp);
    }

    public void clearExistingOTP(LoginInfo loginInfo) {
        Optional<OTP> existingOtp = otpRepository.findByIdLogin(loginInfo);
        if (existingOtp.isPresent()) {
            OTP otp = existingOtp.get();
            deleteOTP(otp);
        }
    }

    public Boolean checkOTP(OTP otp, String loginId, String code) {
        return otp.getIdLogin().getId().equals(loginId) && otp.getCode().equals(code)
                && !otp.getExpiryTime().isBefore(LocalDateTime.now());
    }

    public boolean verifyOTP(String loginId, String code, Boolean isRegistration) {
        List<OTP> otps = otpRepository.findAll();
        for (OTP otp : otps) {
            if (checkOTP(otp, loginId, code)) {
                if ((isRegistration && !otp.getIsRegistration()) || (!isRegistration && otp.getIsRegistration()))
                    return false;
                return true;
            }
        }
        return false;
    }

    public String verifyOTPReturnFcmTokenEmail(String loginId, String code, Boolean isRegistration) {
        List<OTP> otps = otpRepository.findAll();
        for (OTP otp : otps) {
            if (checkOTP(otp, loginId, code)) {
                if ((isRegistration && !otp.getIsRegistration()) || (!isRegistration && otp.getIsRegistration()))
                    return null;
                String fcmToken = otp.getFcmTokenEmail();
                return fcmToken;
            }
        }
        return null;
    }
}
