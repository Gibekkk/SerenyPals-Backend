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
import com.serenypals.restfulapi.model.VirtualDiary;
import com.serenypals.restfulapi.dto.DiaryDTO;
import com.serenypals.restfulapi.service.AuthService;
import com.serenypals.restfulapi.service.VirtualDiaryService;
import com.serenypals.restfulapi.util.ErrorMessage;
import com.serenypals.restfulapi.util.HTTPCode;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
@RequestMapping(value = "${storage.api-prefix}/diary")
public class DiaryController {

    @Autowired
    private AuthService authService;

    @Autowired
    private VirtualDiaryService virtualDiaryService;

    private Object data = "";

    @PatchMapping("/{diaryId}")
    public ResponseEntity<Object> editDiary(HttpServletRequest request,
            @RequestBody DiaryDTO diaryDTO,
            @PathVariable String diaryId)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (diaryDTO.checkDTO()) {
                if (authService.isSessionAlive(sessionToken)) {
                    if (authService.isSessionUser(sessionToken)) {
                        User user = authService.findLoginInfoByToken(sessionToken).get().getIdUser();
                        Optional<VirtualDiary> optionalDiary = virtualDiaryService.findVirtualDiaryById(diaryId);
                        if (optionalDiary.isPresent()) {
                            VirtualDiary diary = optionalDiary.get();
                            if (diary.getIdUser().equals(user)) {
                                diary = virtualDiaryService.editDiary(diary, diaryDTO);
                                data = Map.ofEntries(
                                        Map.entry("id", diary.getId()),
                                        Map.entry("userId", diary.getIdUser().getId()),
                                        Map.entry("judul", virtualDiaryService.decodeDiary(diary.getJudul())),
                                        Map.entry("content", virtualDiaryService.decodeDiary(diary.getContent())),
                                        Map.entry("createdAt", diary.getCreatedAt().toString()),
                                        Map.entry("editedAt", diary.getEditedAt().toString()),
                                        Map.entry("emote", diary.getEmoji()));
                            } else {
                                httpCode = HTTPCode.FORBIDDEN;
                                data = new ErrorMessage(httpCode, "Bukan Diary Anda");
                            }
                        } else {
                            httpCode = HTTPCode.NOT_FOUND;
                            data = new ErrorMessage(httpCode, "Diary tidak ditemukan");
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
                data = new ErrorMessage(httpCode, "Data Diary Tidak Valid");
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

    @DeleteMapping("/{diaryId}")
    public ResponseEntity<Object> deleteDiary(HttpServletRequest request,
            @PathVariable String diaryId)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (authService.isSessionAlive(sessionToken)) {
                if (authService.isSessionUser(sessionToken)) {
                    User user = authService.findLoginInfoByToken(sessionToken).get().getIdUser();
                    Optional<VirtualDiary> optionalDiary = virtualDiaryService.findVirtualDiaryById(diaryId);
                    if (optionalDiary.isPresent()) {
                        VirtualDiary diary = optionalDiary.get();
                        if (diary.getIdUser().equals(user)) {
                            virtualDiaryService.deleteDiary(diary);
                            data = Map.of("Status", "Virtual Diary Dihapus");
                        } else {
                            httpCode = HTTPCode.FORBIDDEN;
                            data = new ErrorMessage(httpCode, "Bukan Diary Anda");
                        }
                    } else {
                        httpCode = HTTPCode.NOT_FOUND;
                        data = new ErrorMessage(httpCode, "Diary tidak ditemukan");
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

    @PostMapping
    public ResponseEntity<Object> createNewDiary(HttpServletRequest request,
            @RequestBody DiaryDTO diaryDTO)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (diaryDTO.checkDTO()) {
                if (authService.isSessionAlive(sessionToken)) {
                    if (authService.isSessionUser(sessionToken)) {
                        User user = authService.findLoginInfoByToken(sessionToken).get().getIdUser();
                        VirtualDiary newDiary = virtualDiaryService.createDiary(diaryDTO, user);
                        data = Map.ofEntries(
                                Map.entry("id", newDiary.getId()),
                                Map.entry("userId", newDiary.getIdUser().getId()),
                                Map.entry("judul", virtualDiaryService.decodeDiary(newDiary.getJudul())),
                                Map.entry("content", virtualDiaryService.decodeDiary(newDiary.getContent())),
                                Map.entry("createdAt", newDiary.getCreatedAt().toString()),
                                Map.entry("editedAt", newDiary.getEditedAt().toString()),
                                Map.entry("emote", newDiary.getEmoji()));
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
                data = new ErrorMessage(httpCode, "Data Diary Tidak Valid");
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
    public ResponseEntity<Object> getAllDiary(HttpServletRequest request)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (authService.isSessionAlive(sessionToken)) {
                if (authService.isSessionUser(sessionToken)) {
                    User user = authService.findLoginInfoByToken(sessionToken).get().getIdUser();
                    List<VirtualDiary> allDiaries = virtualDiaryService.findAllVirtualDiariesByUser(user);
                    ArrayList<Object> response = new ArrayList<Object>();
                    for (VirtualDiary diary : allDiaries) {
                        response.add(Map.ofEntries(
                                Map.entry("id", diary.getId()),
                                Map.entry("userId", diary.getIdUser().getId()),
                                Map.entry("judul", virtualDiaryService.decodeDiary(diary.getJudul())),
                                Map.entry("content", virtualDiaryService.decodeDiary(diary.getContent())),
                                Map.entry("createdAt", diary.getCreatedAt().toString()),
                                Map.entry("editedAt", diary.getEditedAt().toString()),
                                Map.entry("emote", diary.getEmoji())));
                    }
                    data = response;
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

    @GetMapping("/{diaryId}")
    public ResponseEntity<Object> getDiaryById(HttpServletRequest request, @PathVariable String diaryId)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (authService.isSessionAlive(sessionToken)) {
                if (authService.isSessionUser(sessionToken)) {
                    User user = authService.findLoginInfoByToken(sessionToken).get().getIdUser();
                    Optional<VirtualDiary> optionalDiary = virtualDiaryService.findVirtualDiaryById(diaryId);
                    if (optionalDiary.isPresent()) {
                        VirtualDiary diary = optionalDiary.get();
                        if (diary.getIdUser().equals(user)) {
                            data = Map.ofEntries(
                                    Map.entry("id", diary.getId()),
                                    Map.entry("userId", diary.getIdUser().getId()),
                                    Map.entry("judul", virtualDiaryService.decodeDiary(diary.getJudul())),
                                    Map.entry("content", virtualDiaryService.decodeDiary(diary.getContent())),
                                    Map.entry("createdAt", diary.getCreatedAt().toString()),
                                    Map.entry("editedAt", diary.getEditedAt().toString()),
                                    Map.entry("emote", diary.getEmoji()));
                        } else {
                            httpCode = HTTPCode.FORBIDDEN;
                            data = new ErrorMessage(httpCode, "Bukan Diary Anda");
                        }
                    } else {
                        httpCode = HTTPCode.NOT_FOUND;
                        data = new ErrorMessage(httpCode, "Diary tidak ditemukan");
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