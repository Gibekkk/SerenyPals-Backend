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
import com.serenypals.restfulapi.model.LoginInfo;
import com.serenypals.restfulapi.model.SharingForum;
import com.serenypals.restfulapi.dto.UserDTO;
import com.serenypals.restfulapi.service.AuthService;
import com.serenypals.restfulapi.service.SharingForumService;
import com.serenypals.restfulapi.util.ErrorMessage;
import com.serenypals.restfulapi.util.HTTPCode;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
@RequestMapping(value = "${storage.api-prefix}/forum")
public class ForumController {

    @Autowired
    private AuthService authService;

    @Autowired
    private SharingForumService sharingForumService;

    private Object data = "";

    @GetMapping
    public ResponseEntity<Object> getAllForum(HttpServletRequest request)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (authService.isSessionAlive(sessionToken)) {
                if (authService.isSessionUser(sessionToken)) {
                    List<SharingForum> allForums = sharingForumService.findAllForums();
                    ArrayList<Object> response = new ArrayList<Object>();
                    for (SharingForum forum : allForums) {
                        response.add(Map.of(
                            "id", forum.getId(),
                            "userId", forum.getIdUser().getId(),
                            "judul", forum.getJudul(),
                            "content", forum.getContent(),
                            "createdAt", forum.getCreatedAt().toString(),
                            "editedAt", forum.getEditedAt().toString(),
                            "likeCount", forum.getLikeCount(),
                            "commentCount", forum.getCommentCount(),
                            "isSelfForum", forum.getIdUser() == authService.findLoginInfoByToken(sessionToken).get().getIdUser()
                        ));
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

    @GetMapping("/{forumId}")
    public ResponseEntity<Object> getForumById(HttpServletRequest request, @PathVariable String forumId)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
                if (authService.isSessionAlive(sessionToken)) {
                    if (authService.isSessionUser(sessionToken)) {
                        Optional<SharingForum> optionalForum = sharingForumService.findForumById(forumId);
                        if (optionalForum.isPresent()) {
                            SharingForum forum = optionalForum.get();
                            data = Map.of(
                                    "id", forum.getId(),
                                    "userId", forum.getIdUser().getId(),
                                    "judul", forum.getJudul(),
                                    "content", forum.getContent(),
                                    "createdAt", forum.getCreatedAt().toString(),
                                    "editedAt", forum.getEditedAt().toString(),
                                    "likeCount", forum.getLikeCount(),
                                    "commentCount", forum.getCommentCount(),
                                    "isSelfForum", forum.getIdUser() == authService.findLoginInfoByToken(sessionToken).get().getIdUser()
                            );
                        } else {
                            httpCode = HTTPCode.NOT_FOUND;
                            data = new ErrorMessage(httpCode, "Forum tidak ditemukan");
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