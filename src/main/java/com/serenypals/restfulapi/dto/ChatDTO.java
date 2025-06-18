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
public class ChatDTO {
    private String chat;

    public boolean checkDTO() {
        trim();
        if (this.chat == null)
            throw new IllegalArgumentException("Chat Tidak Boleh Bernilai NULL");
        return this.chat != null && checkLength();
    }

    public boolean checkLength() {
        boolean chat = Optional.ofNullable(this.chat)
                .map(s -> s.length() <= 65535)
                .orElse(true);

        if (!chat)
            throw new IllegalArgumentException("Chat Tidak Valid atau Melewati Batas Karakter");

        return chat;
    }

    public void trim() {
        this.chat = Optional.ofNullable(this.chat).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
    }
}
