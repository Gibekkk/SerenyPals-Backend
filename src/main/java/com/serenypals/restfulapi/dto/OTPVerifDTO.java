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
public class OTPVerifDTO {
    private String code;
    private String loginId;

    public boolean checkDTO() {
        trim();
        if(this.code == null) throw new IllegalArgumentException("Kode OTP Tidak Boleh Bernilai NULL");
        if(this.loginId == null) throw new IllegalArgumentException("ID Login Tidak Boleh Bernilai NULL");
        return code != null && loginId != null;
    }

    public void trim() {
        this.code = Optional.ofNullable(this.code).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
        this.loginId = Optional.ofNullable(this.loginId).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
    }

}
