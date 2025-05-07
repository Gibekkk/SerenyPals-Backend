package com.serenypals.restfulapi.controller;

import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.serenypals.restfulapi.util.ErrorMessage;
import com.serenypals.restfulapi.util.HTTPCode;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.core.io.UrlResource;

import java.nio.file.Paths;
import java.util.Base64;

@RestController
@CrossOrigin
@RequestMapping(value = "${storage.api-prefix}/images")
public class ImageController {

    private Object data = "";

    @GetMapping("/**")
    public ResponseEntity<Object> getImage(HttpServletRequest request) throws Exception {
        HTTPCode httpCode = HTTPCode.OK;
        try {
            String pathImage = request.getRequestURI()
                    .substring(request.getContextPath().length() + "/api/v1/images/".length());
            byte[] decodedBytes = Base64.getDecoder().decode(pathImage);
            String fullImagePath = new String(decodedBytes);
            Path path = Paths.get(fullImagePath);
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() && resource.isReadable()) {
                data = resource;
            } else {
                httpCode = HTTPCode.NOT_FOUND;
                data = new ErrorMessage(httpCode, "Gambar Tidak Ditemukan");
            }
        } catch (IllegalArgumentException e) {
            httpCode = HTTPCode.BAD_REQUEST;
            data = new ErrorMessage(httpCode, e.getMessage());
        } catch (Exception e) {
            httpCode = HTTPCode.INTERNAL_SERVER_ERROR;
            data = new ErrorMessage(httpCode, e.getMessage());
        }
        return ResponseEntity
                .status(httpCode.getStatus())
                .contentType(httpCode == HTTPCode.OK ? MediaType.IMAGE_PNG : MediaType.APPLICATION_JSON)
                .body(data);
    }
}