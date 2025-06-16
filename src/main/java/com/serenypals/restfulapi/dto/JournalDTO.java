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
public class JournalDTO {
    private String gangguan;
    private String cerita;
    private int skalaMood;

    public boolean checkDTO() {
        trim();
        boolean skalaMood = this.skalaMood <= 5 && this.skalaMood >= 1;
        if (!skalaMood)
            throw new IllegalArgumentException("Skala Mood Tidak Valid");
        return skalaMood && checkLength();
    }

    public boolean checkLength() {
        boolean gangguan = Optional.ofNullable(this.gangguan)
                .map(s -> s.length() <= 255)
                .orElse(true);
        boolean cerita = Optional.ofNullable(this.cerita)
                .map(s -> s.length() <= 65535)
                .orElse(true);

        if (!gangguan)
            throw new IllegalArgumentException("Faktor Pengganggu Melewati Batas Karakter");
        if (!cerita)
            throw new IllegalArgumentException("Cerita Melewati Batas Karakter");
        return gangguan && cerita;
    }

    public void trim() {
        this.gangguan = Optional.ofNullable(this.gangguan).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
        this.cerita = Optional.ofNullable(this.cerita).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
    }
}
