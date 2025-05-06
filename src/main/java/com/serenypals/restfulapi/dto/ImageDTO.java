package com.serenypals.restfulapi.dto;

import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.image.BufferedImage;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageDTO {
    private MultipartFile image;

    public Boolean checkDTO() {
        if (this.image != null && !this.image.isEmpty()) {
            try {
                BufferedImage image = ImageIO.read(this.image.getInputStream());
                if(image == null) throw new IllegalArgumentException("Gambar Tidak Dapat Diakses");
                return image != null;
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("Gambar Tidak Dapat Diakses");
            }
        }
        throw new IllegalArgumentException("Gambar Kosong");
    }
}