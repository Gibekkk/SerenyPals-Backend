package com.serenypals.restfulapi.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.serenypals.restfulapi.dto.LoginDTO;
import com.serenypals.restfulapi.dto.OTPVerifDTO;
import com.serenypals.restfulapi.dto.UserDTO;
import com.serenypals.restfulapi.dto.EmailDTO;
import com.serenypals.restfulapi.dto.EmailDetails;
import com.serenypals.restfulapi.model.OTP;
import com.serenypals.restfulapi.model.LoginInfo;
import com.serenypals.restfulapi.service.AuthService;
import com.serenypals.restfulapi.service.EmailService;
import com.serenypals.restfulapi.service.OTPService;
import com.serenypals.restfulapi.util.ErrorMessage;
import com.serenypals.restfulapi.util.HTTPCode;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
@RequestMapping(value = "${storage.api-prefix}/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private OTPService otpService;

    @Autowired
    private EmailService emailService;

    private Object data = "";

    @PostMapping("/login")
    public ResponseEntity<Object> login(HttpServletRequest request, @RequestBody LoginDTO loginDTO) throws Exception {
        // String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (loginDTO.checkDTO()) {
                String token = authService.login(loginDTO);
                if (token != null) {
                    data = Map.of("token", token);
                } else {
                    httpCode = HTTPCode.UNAUTHORIZED;
                    data = new ErrorMessage(httpCode, "Login Gagal, Email atau Password Salah");
                }
            } else {
                httpCode = HTTPCode.BAD_REQUEST;
                data = new ErrorMessage(httpCode, "Login Gagal, Data Tidak Valid");
            }
        } catch (IllegalArgumentException e) {
            httpCode = HTTPCode.BAD_REQUEST;
            data = new ErrorMessage(httpCode, e.getMessage());
        } catch (Exception e) {
            httpCode = HTTPCode.INTERNAL_SERVER_ERROR;
            data = new ErrorMessage(httpCode, e.getMessage());
        }
        return ResponseEntity
                .status(httpCode.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(data);
    }

    @GetMapping("/{idLogin}/refreshOtp")
    public ResponseEntity<Object> refreshOtp(HttpServletRequest request, @PathVariable String idLogin)
            throws Exception {
        // String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            Optional<LoginInfo> loginInfoOptional = authService.findLoginInfoByIdLogin(idLogin);
            if (loginInfoOptional.isPresent()) {
                LoginInfo loginInfo = loginInfoOptional.get();
                Optional<OTP> otpOptional = otpService.refreshOTP(loginInfo);
                if (otpOptional.isPresent()) {
                    OTP otp = otpOptional.get();
                    String code = otp.getCode();
                    String expiryTime = otp.getExpiryTime().toString();
                    EmailDetails email = new EmailDetails();
                    email.setRecipient(loginInfo.getEmail());
                    if (otp.getIsRegistration()) {
                        email.setSubject("Verifikasi Akun SerenyPals");
                        email.setMsgBody("Kode OTP Anda: " + code + "\n" +
                                "Berlaku selama 1 menit setelah anda menerima pesan ini\n" +
                                "Silakan masukkan kode ini untuk menyelesaikan pendaftaran.");
                    } else {
                        email.setSubject("Verifikasi Ganti Email");
                        email.setMsgBody("Kode OTP Anda: " + code + "\n" +
                                "Berlaku selama 1 menit setelah anda menerima pesan ini\n" +
                                "Silakan masukkan kode ini untuk mengganti email Anda.");
                    }
                    emailService.sendEmail(email);
                    data = Map.of("loginId", loginInfo.getId(), "expiryTime", expiryTime);
                } else {
                    httpCode = HTTPCode.NOT_FOUND;
                    data = new ErrorMessage(httpCode, "Refresh OTP Gagal, OTP Tidak Ditemukan");
                }
            } else {
                httpCode = HTTPCode.NOT_FOUND;
                data = new ErrorMessage(httpCode, "Refresh OTP Gagal, ID Login Tidak Ditemukan");
            }
        } catch (IllegalArgumentException e) {
            httpCode = HTTPCode.BAD_REQUEST;
            data = new ErrorMessage(httpCode, e.getMessage());
        } catch (Exception e) {
            httpCode = HTTPCode.INTERNAL_SERVER_ERROR;
            data = new ErrorMessage(httpCode, e.getMessage());
        }
        return ResponseEntity
                .status(httpCode.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(data);
    }

    @DeleteMapping("/{idLogin}")
    public ResponseEntity<Object> deleteOTP(HttpServletRequest request, @PathVariable String idLogin)
            throws Exception {
        // String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            Optional<LoginInfo> loginInfoOptional = authService.findLoginInfoByIdLogin(idLogin);
            if (loginInfoOptional.isPresent()) {
                LoginInfo loginInfo = loginInfoOptional.get();
                Optional<OTP> otpOptional = otpService.findOTPByLoginInfo(loginInfo);
                if (otpOptional.isPresent()) {
                    OTP otp = otpOptional.get();
                    otpService.deleteOTP(otp);
                    data = Map.of("status", "OTP Deleted");
                } else {
                    httpCode = HTTPCode.NOT_FOUND;
                    data = new ErrorMessage(httpCode, "Refresh OTP Gagal, OTP Tidak Ditemukan");
                }
            } else {
                httpCode = HTTPCode.NOT_FOUND;
                data = new ErrorMessage(httpCode, "Refresh OTP Gagal, ID Login Tidak Ditemukan");
            }
        } catch (IllegalArgumentException e) {
            httpCode = HTTPCode.BAD_REQUEST;
            data = new ErrorMessage(httpCode, e.getMessage());
        } catch (Exception e) {
            httpCode = HTTPCode.INTERNAL_SERVER_ERROR;
            data = new ErrorMessage(httpCode, e.getMessage());
        }
        return ResponseEntity
                .status(httpCode.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(data);
    }

    @PostMapping("/verifyOtp/register")
    public ResponseEntity<Object> verifyOtpRegister(HttpServletRequest request, @RequestBody OTPVerifDTO otpVerifDTO)
            throws Exception {
        // String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (otpVerifDTO.checkDTO()) {
                Optional<LoginInfo> loginInfoOptional = authService.findLoginInfoByIdLogin(otpVerifDTO.getLoginId());
                if (loginInfoOptional.isPresent()) {
                    LoginInfo loginInfo = loginInfoOptional.get();
                    String fcmToken = otpService.verifyOTPReturnFcmTokenEmail(otpVerifDTO.getLoginId(),
                            otpVerifDTO.getCode(), true);
                    if (fcmToken != null) {
                        String token = authService.verifyLoginInfo(loginInfo, fcmToken);
                        EmailDetails email = new EmailDetails();
                        email.setRecipient(loginInfo.getEmail());
                        email.setSubject("Selamat Bergabung di SerenyPals");
                        email.setMsgBody("Selamat Bergabung di SerenyPals\n" +
                                "Akun Anda telah berhasil diverifikasi.\n" +
                                "Terimakasih sudah bergabung dengan komunitas kami.");
                        emailService.sendEmail(email);
                        data = Map.of("token", token);
                    } else {
                        httpCode = HTTPCode.NOT_FOUND;
                        data = new ErrorMessage(httpCode, "Verify OTP Gagal, OTP Tidak Valid");
                    }
                } else {
                    httpCode = HTTPCode.NOT_FOUND;
                    data = new ErrorMessage(httpCode, "Verify OTP Gagal, ID Login Tidak Ditemukan");
                }
            } else {
                httpCode = HTTPCode.BAD_REQUEST;
                data = new ErrorMessage(httpCode, "Verify OTP Gagal, Data TIdak Valid");
            }
        } catch (IllegalArgumentException e) {
            httpCode = HTTPCode.BAD_REQUEST;
            data = new ErrorMessage(httpCode, e.getMessage());
        } catch (Exception e) {
            httpCode = HTTPCode.INTERNAL_SERVER_ERROR;
            data = new ErrorMessage(httpCode, e.getMessage());
        }
        return ResponseEntity
                .status(httpCode.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(data);
    }

    @PostMapping("/verifyOtp/changeEmail")
    public ResponseEntity<Object> verifyOtpChangeEmail(HttpServletRequest request, @RequestBody OTPVerifDTO otpVerifDTO)
            throws Exception {
        // String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (otpVerifDTO.checkDTO()) {
                Optional<LoginInfo> loginInfoOptional = authService.findLoginInfoByIdLogin(otpVerifDTO.getLoginId());
                if (loginInfoOptional.isPresent()) {
                    LoginInfo loginInfo = loginInfoOptional.get();
                    String emailAddress = otpService.verifyOTPReturnFcmTokenEmail(otpVerifDTO.getLoginId(),
                            otpVerifDTO.getCode(), false);
                    if (emailAddress != null) {
                        authService.changeEmailOTP(loginInfo, emailAddress);
                        EmailDetails email = new EmailDetails();
                        email.setRecipient(emailAddress);
                        email.setSubject("Email Anda diganti");
                        email.setMsgBody("Email Anda telah berhasil diganti");
                        emailService.sendEmail(email);
                        data = Map.of("email", emailAddress);
                    } else {
                        httpCode = HTTPCode.NOT_FOUND;
                        data = new ErrorMessage(httpCode, "Verify OTP Gagal, OTP Tidak Valid");
                    }
                } else {
                    httpCode = HTTPCode.NOT_FOUND;
                    data = new ErrorMessage(httpCode, "Verify OTP Gagal, ID Login Tidak Ditemukan");
                }
            } else {
                httpCode = HTTPCode.BAD_REQUEST;
                data = new ErrorMessage(httpCode, "Verify OTP Gagal, Data TIdak Valid");
            }
        } catch (IllegalArgumentException e) {
            httpCode = HTTPCode.BAD_REQUEST;
            data = new ErrorMessage(httpCode, e.getMessage());
        } catch (Exception e) {
            httpCode = HTTPCode.INTERNAL_SERVER_ERROR;
            data = new ErrorMessage(httpCode, e.getMessage());
        }
        return ResponseEntity
                .status(httpCode.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(data);
    }

    @PostMapping("/register/user")
    public ResponseEntity<Object> registerUser(HttpServletRequest request, @RequestBody UserDTO userDTO)
            throws Exception {
        // String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (userDTO.checkFullDTO()) {
                if (authService.emailAvailable(userDTO.getEmail())) {
                    LoginInfo loginInfo = authService.register(userDTO);
                    OTP otp = otpService.generateOTP(loginInfo, true, userDTO.getFcmToken());
                    String code = otp.getCode();
                    String expiryTime = otp.getExpiryTime().toString();
                    EmailDetails email = new EmailDetails();
                    email.setRecipient(userDTO.getEmail());
                    email.setSubject("Verifikasi Akun SerenyPals");
                    email.setMsgBody("Kode OTP Anda: " + code + "\n" +
                            "Berlaku selama 1 menit setelah anda menerima pesan ini\n" +
                            "Silakan masukkan kode ini untuk menyelesaikan pendaftaran.");
                    emailService.sendEmail(email);
                    data = Map.of("loginId", loginInfo.getId(), "expiryTime", expiryTime);
                } else {
                    httpCode = HTTPCode.CONFLICT;
                    data = new ErrorMessage(httpCode, "Email Sudah Terdaftar");
                }
            } else {
                httpCode = HTTPCode.BAD_REQUEST;
                data = new ErrorMessage(httpCode, "Login Gagal, Data Tidak Valid");
            }
        } catch (IllegalArgumentException e) {
            httpCode = HTTPCode.BAD_REQUEST;
            data = new ErrorMessage(httpCode, e.getMessage());
        } catch (Exception e) {
            httpCode = HTTPCode.INTERNAL_SERVER_ERROR;
            data = new ErrorMessage(httpCode, e.getMessage());
        }
        return ResponseEntity
                .status(httpCode.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(data);
    }

    @PostMapping("/{idLogin}/changeEmail")
    public ResponseEntity<Object> changeEmailWithID(HttpServletRequest request, @RequestBody EmailDTO emailDTO,
            @PathVariable String idLogin)
            throws Exception {
        // String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (emailDTO.checkDTO()) {
                Optional<LoginInfo> loginInfoOptional = authService.findLoginInfoByIdLogin(idLogin);
                if (loginInfoOptional.isPresent()) {
                    LoginInfo loginInfo = loginInfoOptional.get();
                    if (authService.emailEditable(emailDTO.getEmail(), loginInfo)
                            && otpService.emailAvailable(emailDTO.getEmail())) {
                        if (loginInfo.getVerifiedAt() != null) {
                            OTP otp = otpService.generateOTP(loginInfo, false, emailDTO.getEmail());
                            String code = otp.getCode();
                            String expiryTime = otp.getExpiryTime().toString();
                            EmailDetails email = new EmailDetails();
                            email.setRecipient(emailDTO.getEmail());
                            email.setSubject("Verifikasi Ganti Email");
                            email.setMsgBody("Kode OTP Anda: " + code + "\n" +
                                    "Berlaku selama 1 menit setelah anda menerima pesan ini\n" +
                                    "Silakan masukkan kode ini untuk mengganti email Anda.");
                            emailService.sendEmail(email);
                            data = Map.of("loginId", loginInfo.getId(), "expiryTime", expiryTime);
                        } else {
                            String editedEmail = authService.changeEmailOTP(loginInfo, emailDTO.getEmail());
                            Optional<OTP> otpOptional = otpService.refreshOTP(loginInfo);
                            if (otpOptional.isPresent()) {
                                OTP otp = otpOptional.get();
                                String code = otp.getCode();
                                String expiryTime = otp.getExpiryTime().toString();
                                EmailDetails email = new EmailDetails();
                                email.setRecipient(editedEmail);
                                email.setSubject("Verifikasi Akun SerenyPals");
                                email.setMsgBody("Kode OTP Anda: " + code + "\n" +
                                        "Berlaku selama 1 menit setelah anda menerima pesan ini\n" +
                                        "Silakan masukkan kode ini untuk menyelesaikan pendaftaran.");
                                emailService.sendEmail(email);
                                data = Map.of("loginId", loginInfo.getId(), "expiryTime", expiryTime);
                            } else {
                                httpCode = HTTPCode.NOT_FOUND;
                                data = new ErrorMessage(httpCode, "Ganti Email Registrasi Gagal, OTP Tidak Ditemukan");
                            }
                        }
                    } else {
                        httpCode = HTTPCode.CONFLICT;
                        data = new ErrorMessage(httpCode, "Email Sudah Terdaftar");
                    }
                } else {
                    httpCode = HTTPCode.NOT_FOUND;
                    data = new ErrorMessage(httpCode, "Gagal Mengganti Email, ID Login Tidak Ditemukan");
                }
            } else {
                httpCode = HTTPCode.BAD_REQUEST;
                data = new ErrorMessage(httpCode, "Pergantian Email Gagal, Data Tidak Valid");
            }
        } catch (IllegalArgumentException e) {
            httpCode = HTTPCode.BAD_REQUEST;
            data = new ErrorMessage(httpCode, e.getMessage());
        } catch (Exception e) {
            httpCode = HTTPCode.INTERNAL_SERVER_ERROR;
            data = new ErrorMessage(httpCode, e.getMessage());
        }
        return ResponseEntity
                .status(httpCode.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(data);
    }

    @PostMapping("/changeEmail")
    public ResponseEntity<Object> changeEmailWithToken(HttpServletRequest request, @RequestBody EmailDTO emailDTO)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (emailDTO.checkDTO()) {
                Optional<LoginInfo> loginInfoOptional = authService.findLoginInfoByToken(sessionToken);
                if (loginInfoOptional.isPresent()) {
                    LoginInfo loginInfo = loginInfoOptional.get();
                    if (authService.emailEditable(emailDTO.getEmail(), loginInfo)
                            && otpService.emailAvailable(emailDTO.getEmail())) {
                            OTP otp = otpService.generateOTP(loginInfo, false, emailDTO.getEmail());
                            String code = otp.getCode();
                            String expiryTime = otp.getExpiryTime().toString();
                            EmailDetails email = new EmailDetails();
                            email.setRecipient(emailDTO.getEmail());
                            email.setSubject("Verifikasi Ganti Email");
                            email.setMsgBody("Kode OTP Anda: " + code + "\n" +
                                    "Berlaku selama 1 menit setelah anda menerima pesan ini\n" +
                                    "Silakan masukkan kode ini untuk mengganti email Anda.");
                            emailService.sendEmail(email);
                            data = Map.of("loginId", loginInfo.getId(), "expiryTime", expiryTime);
                    } else {
                        httpCode = HTTPCode.CONFLICT;
                        data = new ErrorMessage(httpCode, "Email Sudah Terdaftar");
                    }
                } else {
                    httpCode = HTTPCode.NOT_FOUND;
                    data = new ErrorMessage(httpCode, "Gagal Mengganti Email, Token Invalid");
                }
            } else {
                httpCode = HTTPCode.BAD_REQUEST;
                data = new ErrorMessage(httpCode, "Pergantian Email Gagal, Data Tidak Valid");
            }
        } catch (IllegalArgumentException e) {
            httpCode = HTTPCode.BAD_REQUEST;
            data = new ErrorMessage(httpCode, e.getMessage());
        } catch (Exception e) {
            httpCode = HTTPCode.INTERNAL_SERVER_ERROR;
            data = new ErrorMessage(httpCode, e.getMessage());
        }
        return ResponseEntity
                .status(httpCode.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(data);
    }
}