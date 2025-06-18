package com.serenypals.restfulapi.util;

import java.util.Base64;

import org.springframework.context.annotation.Configuration;

@Configuration
public class Base64Converter {
    public String encrypt(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes());
    }

    public String decrypt(String encodedText) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedText);
        String decodedString = new String(decodedBytes);
        return decodedString;
    }
}
