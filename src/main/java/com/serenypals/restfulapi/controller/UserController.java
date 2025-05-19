package com.serenypals.restfulapi.controller;

import java.util.Map;
import java.util.Optional;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.serenypals.restfulapi.model.User;
import com.serenypals.restfulapi.model.LoginInfo;
import com.serenypals.restfulapi.model.SerenyPremiumUser;
import com.serenypals.restfulapi.dto.UserDTO;
import com.serenypals.restfulapi.service.AuthService;
import com.serenypals.restfulapi.util.ErrorMessage;
import com.serenypals.restfulapi.util.HTTPCode;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
@RequestMapping(value = "${storage.api-prefix}/user")
public class UserController {

    @Autowired
    private AuthService authService;

    private Object data = "";

    @GetMapping("/profile")
    public ResponseEntity<Object> getProfileUser(HttpServletRequest request)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (authService.isSessionAlive(sessionToken)) {
                if (authService.isSessionUser(sessionToken)) {
                    LoginInfo loginInfo = authService.findLoginInfoByToken(sessionToken).get();
                    User user = loginInfo.getIdUser();
                    SerenyPremiumUser userSerenyPremium = user.getUserSerenyPremium();
                    data = Map.of(
                            "id_login", loginInfo.getId(),
                            "id_user", user.getId(),
                            "nama", user.getNama(),
                            "nomor_telepon", user.getNomorTelepon(),
                            "tanggal_lahir", user.getTanggalLahir(),
                            "email", loginInfo.getEmail(),
                            "verified_at", loginInfo.getVerifiedAt().toString(),
                            "sereny_premium_end", userSerenyPremium.getEndAt().isBefore(LocalDate.now()) ? ""
                                    : userSerenyPremium.getEndAt().toString());
                } else {
                    httpCode = HTTPCode.FORBIDDEN;
                    data = new ErrorMessage(httpCode, "Akses Anda Ditolak Untuk Fitur Ini");
                }
            } else {
                httpCode = HTTPCode.UNAUTHORIZED;
                data = new ErrorMessage(httpCode, "Session Token Tidak Valid, Mohon Melakukan Login Kembali");
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

    @PatchMapping("/profile")
    public ResponseEntity<Object> editProfileUser(HttpServletRequest request, @RequestBody UserDTO userDTO)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (userDTO.checkDTO()) {
                if (authService.isSessionAlive(sessionToken)) {
                    if (authService.isSessionUser(sessionToken)) {
                        LoginInfo loginInfo = authService.editUser(userDTO, authService.findLoginInfoByToken(sessionToken).get());
                        User user = loginInfo.getIdUser();
                        SerenyPremiumUser userSerenyPremium = user.getUserSerenyPremium();
                        data = Map.of(
                                "id_login", loginInfo.getId(),
                                "id_user", user.getId(),
                                "nama", user.getNama(),
                                "nomor_telepon", user.getNomorTelepon(),
                                "tanggal_lahir", user.getTanggalLahir(),
                                "email", loginInfo.getEmail(),
                                "verified_at", loginInfo.getVerifiedAt().toString(),
                                "sereny_premium_end", userSerenyPremium.getEndAt().isBefore(LocalDate.now()) ? ""
                                        : userSerenyPremium.getEndAt().toString());
                    } else {
                        httpCode = HTTPCode.FORBIDDEN;
                        data = new ErrorMessage(httpCode, "Akses Anda Ditolak Untuk Fitur Ini");
                    }
                } else {
                    httpCode = HTTPCode.UNAUTHORIZED;
                    data = new ErrorMessage(httpCode, "Session Token Tidak Valid, Mohon Melakukan Login Kembali");
                }
            } else {
                httpCode = HTTPCode.BAD_REQUEST;
                data = new ErrorMessage(httpCode, "Data Form Tidak Valid");
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