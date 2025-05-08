package com.serenypals.restfulapi.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.serenypals.restfulapi.dto.LoginDTO;
import com.serenypals.restfulapi.service.AuthService;
import com.serenypals.restfulapi.util.ErrorMessage;
import com.serenypals.restfulapi.util.HTTPCode;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
@RequestMapping(value = "${storage.api-prefix}/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    private Object data = "";

    @GetMapping("/login")
    public ResponseEntity<Object> login(HttpServletRequest request, @RequestBody LoginDTO loginDTO) throws Exception {
        // String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if(loginDTO.checkDTO()){
                String token = authService.login(loginDTO);
                if (token != null) {
                    data = Map.of("token", token);
                } else {
                    httpCode = HTTPCode.UNAUTHORIZED;
                    data = new ErrorMessage(httpCode, "Login Gagal, Email atau Password Salah");
                }
            } else {
                httpCode = HTTPCode.BAD_REQUEST;
            data = new ErrorMessage(httpCode, "Login Gagal, Data Tidak Valid");
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
                .contentType(MediaType.APPLICATION_JSON)
                .body(data);
    }
}