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
public class PasswordDTO {
        private String oldPassword;
        private String newPassword;
        private String confirmPassword;

        public boolean checkDTO() {
                trim();
                if (this.oldPassword == null)
                        throw new IllegalArgumentException("Password Lama Tidak Boleh Bernilai NULL");
                if (this.newPassword == null)
                        throw new IllegalArgumentException("Password Baru Tidak Boleh Bernilai NULL");
                if (this.confirmPassword == null)
                        throw new IllegalArgumentException("Konfirmasi Password Tidak Boleh Bernilai NULL");
                return this.oldPassword != null &&
                                this.newPassword != null &&
                                this.confirmPassword != null;
        }

        public void trim() {
                this.oldPassword = Optional.ofNullable(this.oldPassword).map(String::trim).filter(s -> !s.isBlank())
                                .orElse(null);
                this.newPassword = Optional.ofNullable(this.newPassword).map(String::trim).filter(s -> !s.isBlank())
                                .orElse(null);
                this.confirmPassword = Optional.ofNullable(this.confirmPassword).map(String::trim)
                                .filter(s -> !s.isBlank())
                                .orElse(null);
        }
}