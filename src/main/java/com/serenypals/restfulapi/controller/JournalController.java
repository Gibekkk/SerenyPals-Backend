package com.serenypals.restfulapi.controller;

import java.util.Map;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.serenypals.restfulapi.model.User;
import com.serenypals.restfulapi.model.MoodJournaling;
import com.serenypals.restfulapi.dto.JournalDTO;
import com.serenypals.restfulapi.service.AuthService;
import com.serenypals.restfulapi.service.MoodJournalingService;
import com.serenypals.restfulapi.util.ErrorMessage;
import com.serenypals.restfulapi.util.HTTPCode;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
@RequestMapping(value = "${storage.api-prefix}/journaling")
public class JournalController {

    @Autowired
    private AuthService authService;

    @Autowired
    private MoodJournalingService moodJournalingService;

    private Object data = "";

    @PostMapping
    public ResponseEntity<Object> createNewJournal(HttpServletRequest request,
            @RequestBody JournalDTO journalDTO)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (journalDTO.checkDTO()) {
                if (authService.isSessionAlive(sessionToken)) {
                    if (authService.isSessionUser(sessionToken)) {
                        User user = authService.findLoginInfoByToken(sessionToken).get().getIdUser();
                        if (!moodJournalingService.getTodayJournalByUser(user).isPresent()) {
                            MoodJournaling newJournal = moodJournalingService.createJournal(journalDTO, user);
                            data = Map.ofEntries(
                                    Map.entry("id", newJournal.getId()),
                                    Map.entry("userId", newJournal.getIdUser().getId()),
                                    Map.entry("gangguan", Optional.ofNullable(newJournal.getGangguan()).orElse("")),
                                    Map.entry("cerita", Optional.ofNullable(newJournal.getCerita()).orElse("")),
                                    Map.entry("createdAt", newJournal.getCreatedAt().toString()),
                                    Map.entry("moodScale", newJournal.getMoodScale()));
                        } else {
                            httpCode = HTTPCode.FORBIDDEN;
                            data = new ErrorMessage(httpCode, "Anda Sudah Mengisi Journal Hari Ini");
                        }
                    } else {
                        httpCode = HTTPCode.FORBIDDEN;
                        data = new ErrorMessage(httpCode, "Akses Anda Ditolak Untuk Fitur Ini");
                    }
                } else {
                    httpCode = HTTPCode.UNAUTHORIZED;
                    data = new ErrorMessage(httpCode, "Session Token Tidak Valid, Mohon Melakukan Login Kembali");
                }
            } else {
                httpCode = HTTPCode.BAD_REQUEST;
                data = new ErrorMessage(httpCode, "Data Journal Tidak Valid");
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

    @GetMapping
    public ResponseEntity<Object> getTodayJournal(HttpServletRequest request)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (authService.isSessionAlive(sessionToken)) {
                if (authService.isSessionUser(sessionToken)) {
                    User user = authService.findLoginInfoByToken(sessionToken).get().getIdUser();
                    Optional<MoodJournaling> optionalJournal = moodJournalingService.getTodayJournalByUser(user);
                    if (optionalJournal.isPresent()) {
                        MoodJournaling todayJournal = optionalJournal.get();
                        data = Map.ofEntries(
                                Map.entry("id", todayJournal.getId()),
                                Map.entry("userId", todayJournal.getIdUser().getId()),
                                Map.entry("gangguan", Optional.ofNullable(todayJournal.getGangguan()).orElse("")),
                                Map.entry("cerita", Optional.ofNullable(todayJournal.getCerita()).orElse("")),
                                Map.entry("createdAt", todayJournal.getCreatedAt().toString()),
                                Map.entry("moodScale", todayJournal.getMoodScale()));
                    } else {
                        httpCode = HTTPCode.NOT_FOUND;
                        data = new ErrorMessage(httpCode, "Anda Belum Mengisi Journal Hari Ini");
                    }
                } else {
                    httpCode = HTTPCode.FORBIDDEN;
                    data = new ErrorMessage(httpCode, "Akses Anda Ditolak Untuk Fitur Ini");
                }
            } else {
                httpCode = HTTPCode.UNAUTHORIZED;
                data = new ErrorMessage(httpCode, "Session Token Tidak Valid, Mohon Melakukan Login Kembali");
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