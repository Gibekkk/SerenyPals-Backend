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
public class CommentDTO {
    private String comment;

    public boolean checkDTO() {
        if (this.comment == null)
            throw new IllegalArgumentException("Komentar Tidak Boleh Bernilai NULL");
        return this.comment != null;
    }

    public boolean checkLength() {
        boolean comment = Optional.ofNullable(this.comment)
                .map(s -> s.length() <= 65535)
                .orElse(true);

        if (!comment)
            throw new IllegalArgumentException("Komentar atau Melewati Batas Karakter");

        return comment;
    }

    public void trim() {
        this.comment = Optional.ofNullable(this.comment).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
    }
}
