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
public class DiaryDTO {
    private String judul;
    private String content;
    private String emote;

    public boolean checkDTO() {
        trim();
        if (this.judul == null)
            throw new IllegalArgumentException("Judul Tidak Boleh Bernilai NULL");
        if (this.content == null)
            throw new IllegalArgumentException("Konten Tidak Boleh Bernilai NULL");
        if (this.emote == null)
            throw new IllegalArgumentException("Emoji Tidak Boleh Bernilai NULL");
        return this.judul != null && this.content != null && this.emote != null && checkLength();
    }

    public boolean checkLength() {
        boolean judul = Optional.ofNullable(this.judul)
                .map(s -> s.length() <= 255)
                .orElse(true);
        boolean content = Optional.ofNullable(this.content)
                .map(s -> s.length() <= 65535)
                .orElse(true);
        boolean emote = Optional.ofNullable(this.emote)
                .map(s -> s.length() <= 1)
                .orElse(true);

        if (!judul)
            throw new IllegalArgumentException("Judul Melewati Batas Karakter");
        if (!content)
            throw new IllegalArgumentException("Konten Melewati Batas Karakter");
        if (!emote)
            throw new IllegalArgumentException("Emoji Melewati Batas Karakter");
        return judul && content && emote;
    }

    public void trim() {
        this.judul = Optional.ofNullable(this.judul).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
        this.content = Optional.ofNullable(this.content).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
        this.emote = Optional.ofNullable(this.emote).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
    }
}
