// package com.serenypals.restfulapi.service;

// import java.util.Optional;
// import java.util.Random;
// import java.util.Map;
// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import com.serenypals.restfulapi.model.OTP;
// import com.serenypals.restfulapi.model.User;
// import com.serenypals.restfulapi.repository.OTPRepository;

// import jakarta.transaction.Transactional;

// @Service
// public class OTPService {

//     @Autowired
//     private OTPRepository otpRepository;

//     @Autowired
//     private CleanUpService cleanUpService;

//     private int OTP_LENGTH = 6;
//     private int OTP_TIME_OUT = 1;

//     // Generate dan simpan OTP
//     public String generateOTP(User user, boolean isRegistration, String fcmToken) {
//         clearExistingOTP(user);
//         String code = String.format("%0" + OTP_LENGTH + "d", new Random().nextInt(999_999));
//         LocalDateTime expiry = LocalDateTime.now().plusMinutes(OTP_TIME_OUT);
//         OTP otp = new OTP();
//         otp.setId(null);
//         otp.setCode(code);
//         otp.setExpiryTime(expiry);
//         otp.setIsRegistration(isRegistration);
//         otp.setIdUser(user);
//         otp.setFcmToken(fcmToken);
//         otpRepository.save(otp);
//         return code;
//     }

//     // Generate dan simpan OTP
//     public String refreshOTP(User user) {
//         String code = String.format("%0" + OTP_LENGTH + "d", new Random().nextInt(999_999));
//         LocalDateTime expiry = LocalDateTime.now().plusMinutes(OTP_TIME_OUT);
//         Optional<OTP> existingOtp = otpRepository.findByIdUser(user);
//         if (existingOtp.isPresent()) {
//             OTP otp = existingOtp.get();
//             otp.setExpiryTime(expiry);
//             otp.setCode(code);
//             otpRepository.save(otp);
//             return code;
//         }
//         return null;
//     }

//     @Transactional
//     public void clearExistingOTP(User user) {
//         Optional<OTP> existingOtp = otpRepository.findByIdUser(user);
//         if (existingOtp.isPresent()) {
//             OTP otp = existingOtp.get();
//             boolean isRegistration = otp.getIsRegistration();
//             otpRepository.delete(otp);
//             if (isRegistration){
//                 // cleanUpService.cleanUser(user);
//             }
//         }
//     }

//     @Transactional
//     public void clearRedundantOTP() {
//         List<OTP> otps = otpRepository.findAll();
//         for (OTP otp : otps) {
//             if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
//                 boolean isRegistration = otp.getIsRegistration();
//                 User user = otp.getIdUser();
//                 otpRepository.delete(otp);
//                 if (isRegistration){
//                     // cleanUpService.cleanUser(user);
//                 }
//             }
//         }
//     }

//     @Transactional
//     public boolean verifyOTP(String userId, String code) {
//         List<OTP> otps = otpRepository.findAll();
//         for (OTP otp : otps) {
//             if (otp.getIdUser().getId().equals(userId) && otp.getCode().equals(code)) {
//                 otpRepository.delete(otp);
//                 return true;
//             }
//         }
//         return false;
//     }

//     @Transactional
//     public String verifyOTPReturnFcmToken(String userId, String code) {
//         List<OTP> otps = otpRepository.findAll();
//         for (OTP otp : otps) {
//             if (otp.getIdUser().getId().equals(userId) && otp.getCode().equals(code)) {
//                 String fcmToken = otp.getFcmToken();
//                 otpRepository.delete(otp);
//                 return fcmToken;
//             }
//         }
//         return null;
//     }
// }
