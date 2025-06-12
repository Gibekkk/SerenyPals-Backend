package com.serenypals.restfulapi.dto;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ForumDTO {
    private String judul;
    private String content;

    public boolean checkDTO() {
        if (this.judul == null)
            throw new IllegalArgumentException("Judul Tidak Boleh Bernilai NULL");
        if (this.content == null)
            throw new IllegalArgumentException("Konten Tidak Boleh Bernilai NULL");
        return this.judul != null && this.content != null;
    }

    public boolean checkLength() {
        boolean judul = Optional.ofNullable(this.judul)
                .map(s -> s.length() <= 255)
                .orElse(true);
        boolean content = Optional.ofNullable(this.content)
                .map(s -> s.length() <= 65535)
                .orElse(true);

        if (!judul)
            throw new IllegalArgumentException("Judul Melewati Batas Karakter");
        if (!content)
            throw new IllegalArgumentException("Konten Melewati Batas Karakter");
        return judul && content;
    }

    public void trim() {
        this.judul = Optional.ofNullable(this.judul).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
        this.content = Optional.ofNullable(this.content).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
    }
}
