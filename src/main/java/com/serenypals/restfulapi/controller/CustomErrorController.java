package com.serenypals.restfulapi.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.serenypals.restfulapi.util.ErrorMessage;
import com.serenypals.restfulapi.util.HTTPCode;
import org.springframework.http.MediaType;

@RestController
public class CustomErrorController implements ErrorController {

    private Object data = "";

    @RequestMapping("/error")
    public ResponseEntity<Object> handleError(HttpServletRequest request) {
        int statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        HTTPCode httpCode = HTTPCode.fromCode(statusCode);
        if (httpCode.equals(HTTPCode.NOT_FOUND)) {
            data = new ErrorMessage(httpCode, "Rute Tidak Ditemukan");
        } else if (httpCode.equals(HTTPCode.METHOD_NOT_ALLOWED)) {
            data = new ErrorMessage(httpCode, "Metode Untuk Rute Ini Salah");
        } else {
            data = new ErrorMessage(httpCode, "Error Tidak Diketahui Terjadi Di Luar API Utama");
        }

        return ResponseEntity
                .status(httpCode.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(data);
    }
}