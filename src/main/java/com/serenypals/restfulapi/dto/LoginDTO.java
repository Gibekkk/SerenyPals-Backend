package com.serenypals.restfulapi.dto;

import lombok.Setter;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    private String email;
    private String username;
    private String password;
    private String fcmToken;

    public boolean checkDTOUser() {
        trim();
        if(this.email == null) throw new IllegalArgumentException("Email Tidak Boleh Bernilai NULL");
        if(this.password == null) throw new IllegalArgumentException("Password Tidak Boleh Bernilai NULL");
        if(this.fcmToken == null) throw new IllegalArgumentException("Token FCM Tidak Boleh Bernilai NULL");
        return email != null && password != null && fcmToken != null;
    }

    public boolean checkDTOAdmin() {
        trim();
        if(this.username == null) throw new IllegalArgumentException("Username Tidak Boleh Bernilai NULL");
        if(this.password == null) throw new IllegalArgumentException("Password Tidak Boleh Bernilai NULL");
        return username != null && password != null;
    }

    public void trim() {
        this.email = Optional.ofNullable(this.email).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
        this.username = Optional.ofNullable(this.username).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
        this.password = Optional.ofNullable(this.password).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
        this.fcmToken = Optional.ofNullable(this.fcmToken).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
    }

}
