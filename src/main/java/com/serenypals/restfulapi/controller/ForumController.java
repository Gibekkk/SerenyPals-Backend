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
import com.serenypals.restfulapi.model.SharingForum;
import com.serenypals.restfulapi.model.SharingForumComments;
import com.serenypals.restfulapi.dto.ForumDTO;
import com.serenypals.restfulapi.dto.CommentDTO;
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

    @PatchMapping("/{commentId}/comment")
    public ResponseEntity<Object> editCommentForum(HttpServletRequest request, @RequestBody CommentDTO commentDTO,
            @PathVariable String commentId)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (commentDTO.checkDTO()) {
                if (authService.isSessionAlive(sessionToken)) {
                    if (authService.isSessionUser(sessionToken)) {
                        User user = authService.findLoginInfoByToken(sessionToken).get().getIdUser();
                        Optional<SharingForumComments> optionalForumComment = sharingForumService.findForumCommentById(commentId);
                        if (optionalForumComment.isPresent()) {
                            SharingForumComments forumComment = optionalForumComment.get();
                            if(forumComment.getIdUser().equals(user)){
                            if (sharingForumService.isContentSafe(commentDTO.getComment())) {
                                SharingForum forum = sharingForumService.editForumComment(forumComment, user, commentDTO.getComment());
                                data = Map.ofEntries(
                                        Map.entry("id", forum.getId()),
                                        Map.entry("userId", forum.getIdUser().getId()),
                                        Map.entry("judul", forum.getJudul()),
                                        Map.entry("content", forum.getContent()),
                                        Map.entry("createdAt", forum.getCreatedAt().toString()),
                                        Map.entry("editedAt", forum.getEditedAt().toString()),
                                        Map.entry("likeCount", sharingForumService.getLikeCount(forum)),
                                        Map.entry("isLiked", sharingForumService.isLiked(forum, user)),
                                        Map.entry("commentCount", sharingForumService.getCommentCount(forum)),
                                        Map.entry("comments", sharingForumService.getComments(forum)),
                                        Map.entry("isSelfForum", forum.getIdUser() == user));
                            } else {
                                httpCode = HTTPCode.FORBIDDEN;
                                data = new ErrorMessage(httpCode,
                                        "Konten Forum Tidak Sopan dan Mungkin Bisa Menyinggung Seseorang");
                            }
                        } else {
                         httpCode = HTTPCode.FORBIDDEN;
                                data = new ErrorMessage(httpCode,
                                        "Bukan Komentar Anda");   
                        }
                        } else {
                            httpCode = HTTPCode.NOT_FOUND;
                            data = new ErrorMessage(httpCode, "Komentar Forum tidak ditemukan");
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
                data = new ErrorMessage(httpCode, "Komentar Tidak Valid");
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

    @DeleteMapping("/{commentId}/comment")
    public ResponseEntity<Object> deleteCommentForum(HttpServletRequest request,
            @PathVariable String commentId)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
                if (authService.isSessionAlive(sessionToken)) {
                    if (authService.isSessionUser(sessionToken)) {
                        User user = authService.findLoginInfoByToken(sessionToken).get().getIdUser();
                        Optional<SharingForumComments> optionalForumComment = sharingForumService.findForumCommentById(commentId);
                        if (optionalForumComment.isPresent()) {
                            SharingForumComments forumComment = optionalForumComment.get();
                            if (forumComment.getIdUser().equals(user)) {
                                sharingForumService.deleteForumComment(forumComment);
                                data = Map.of("Status", "Komentar Dihapus");
                            } else {
                                httpCode = HTTPCode.FORBIDDEN;
                                data = new ErrorMessage(httpCode,
                                        "Bukan Komentar Forum Anda");
                            }
                        } else {
                            httpCode = HTTPCode.NOT_FOUND;
                            data = new ErrorMessage(httpCode, "Komentar Forum tidak ditemukan");
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

    @PostMapping("/{forumId}/comment")
    public ResponseEntity<Object> commentForum(HttpServletRequest request, @RequestBody CommentDTO commentDTO,
            @PathVariable String forumId)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (commentDTO.checkDTO()) {
                if (authService.isSessionAlive(sessionToken)) {
                    if (authService.isSessionUser(sessionToken)) {
                        User user = authService.findLoginInfoByToken(sessionToken).get().getIdUser();
                        Optional<SharingForum> optionalForum = sharingForumService.findForumById(forumId);
                        if (optionalForum.isPresent()) {
                            SharingForum forum = optionalForum.get();
                            if (sharingForumService.isContentSafe(commentDTO.getComment())) {
                                forum = sharingForumService.commentForum(forum, user, commentDTO.getComment());
                                data = Map.ofEntries(
                                        Map.entry("id", forum.getId()),
                                        Map.entry("userId", forum.getIdUser().getId()),
                                        Map.entry("judul", forum.getJudul()),
                                        Map.entry("content", forum.getContent()),
                                        Map.entry("createdAt", forum.getCreatedAt().toString()),
                                        Map.entry("editedAt", forum.getEditedAt().toString()),
                                        Map.entry("likeCount", sharingForumService.getLikeCount(forum)),
                                        Map.entry("isLiked", sharingForumService.isLiked(forum, user)),
                                        Map.entry("commentCount", sharingForumService.getCommentCount(forum)),
                                        Map.entry("comments", sharingForumService.getComments(forum)),
                                        Map.entry("isSelfForum", forum.getIdUser() == user));
                            } else {
                                httpCode = HTTPCode.FORBIDDEN;
                                data = new ErrorMessage(httpCode,
                                        "Konten Forum Tidak Sopan dan Mungkin Bisa Menyinggung Seseorang");
                            }
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
            } else {
                httpCode = HTTPCode.BAD_REQUEST;
                data = new ErrorMessage(httpCode, "Komentar Tidak Valid");
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

    @PostMapping("/{forumId}/toggleLike")
    public ResponseEntity<Object> likeForum(HttpServletRequest request,
            @PathVariable String forumId)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (authService.isSessionAlive(sessionToken)) {
                if (authService.isSessionUser(sessionToken)) {
                    User user = authService.findLoginInfoByToken(sessionToken).get().getIdUser();
                    Optional<SharingForum> optionalForum = sharingForumService.findForumById(forumId);
                    if (optionalForum.isPresent()) {
                        SharingForum forum = optionalForum.get();
                        forum = sharingForumService.toggleLikeForum(forum, user);
                        data = Map.ofEntries(
                                        Map.entry("id", forum.getId()),
                                        Map.entry("userId", forum.getIdUser().getId()),
                                        Map.entry("judul", forum.getJudul()),
                                        Map.entry("content", forum.getContent()),
                                        Map.entry("createdAt", forum.getCreatedAt().toString()),
                                        Map.entry("editedAt", forum.getEditedAt().toString()),
                                        Map.entry("likeCount", sharingForumService.getLikeCount(forum)),
                                        Map.entry("isLiked", sharingForumService.isLiked(forum, user)),
                                        Map.entry("commentCount", sharingForumService.getCommentCount(forum)),
                                        Map.entry("comments", sharingForumService.getComments(forum)),
                                        Map.entry("isSelfForum", forum.getIdUser() == user));
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

    @PatchMapping("/{forumId}")
    public ResponseEntity<Object> editForum(HttpServletRequest request, @RequestBody ForumDTO forumDTO,
            @PathVariable String forumId)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (forumDTO.checkDTO()) {
                if (authService.isSessionAlive(sessionToken)) {
                    if (authService.isSessionUser(sessionToken)) {
                        User user = authService.findLoginInfoByToken(sessionToken).get().getIdUser();
                        if (sharingForumService.isContentSafe(forumDTO.getContent())
                                && sharingForumService.isContentSafe(forumDTO.getJudul())) {
                            Optional<SharingForum> optionalForum = sharingForumService.findForumById(forumId);
                            if (optionalForum.isPresent()) {
                                SharingForum forum = optionalForum.get();
                                if (forum.getIdUser().equals(user)) {
                                    forum = sharingForumService.editForum(forum, forumDTO);
                                    data = Map.ofEntries(
                                        Map.entry("id", forum.getId()),
                                        Map.entry("userId", forum.getIdUser().getId()),
                                        Map.entry("judul", forum.getJudul()),
                                        Map.entry("content", forum.getContent()),
                                        Map.entry("createdAt", forum.getCreatedAt().toString()),
                                        Map.entry("editedAt", forum.getEditedAt().toString()),
                                        Map.entry("likeCount", sharingForumService.getLikeCount(forum)),
                                        Map.entry("isLiked", sharingForumService.isLiked(forum, user)),
                                        Map.entry("commentCount", sharingForumService.getCommentCount(forum)),
                                        Map.entry("comments", sharingForumService.getComments(forum)),
                                        Map.entry("isSelfForum", forum.getIdUser() == user));
                                } else {
                                    httpCode = HTTPCode.FORBIDDEN;
                                    data = new ErrorMessage(httpCode, "Bukan Forum Anda");
                                }
                            } else {
                                httpCode = HTTPCode.NOT_FOUND;
                                data = new ErrorMessage(httpCode, "Forum tidak ditemukan");
                            }
                        } else {
                            httpCode = HTTPCode.FORBIDDEN;
                            data = new ErrorMessage(httpCode,
                                    "Konten Forum Tidak Sopan dan Mungkin Bisa Menyinggung Seseorang");
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
                data = new ErrorMessage(httpCode, "Data Forum Tidak Valid");
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

    @DeleteMapping("/{forumId}")
    public ResponseEntity<Object> deleteForum(HttpServletRequest request,
            @PathVariable String forumId)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (authService.isSessionAlive(sessionToken)) {
                if (authService.isSessionUser(sessionToken)) {
                    User user = authService.findLoginInfoByToken(sessionToken).get().getIdUser();
                    Optional<SharingForum> optionalForum = sharingForumService.findForumById(forumId);
                    if (optionalForum.isPresent()) {
                        SharingForum forum = optionalForum.get();
                        if (forum.getIdUser().equals(user)) {
                            sharingForumService.deleteForum(forum);
                            data = Map.of("Status", "Forum Dihapus");
                        } else {
                            httpCode = HTTPCode.FORBIDDEN;
                            data = new ErrorMessage(httpCode, "Bukan Forum Anda");
                        }
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

    @PostMapping
    public ResponseEntity<Object> createNewForum(HttpServletRequest request, @RequestBody ForumDTO forumDTO)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (forumDTO.checkDTO()) {
                if (authService.isSessionAlive(sessionToken)) {
                    if (authService.isSessionUser(sessionToken)) {
                        User user = authService.findLoginInfoByToken(sessionToken).get().getIdUser();
                        if (sharingForumService.isContentSafe(forumDTO.getContent())
                                && sharingForumService.isContentSafe(forumDTO.getJudul())) {
                            SharingForum newForum = sharingForumService.createForum(forumDTO, user);
                            data = Map.ofEntries(
                                        Map.entry("id", newForum.getId()),
                                        Map.entry("userId", newForum.getIdUser().getId()),
                                        Map.entry("judul", newForum.getJudul()),
                                        Map.entry("content", newForum.getContent()),
                                        Map.entry("createdAt", newForum.getCreatedAt().toString()),
                                        Map.entry("editedAt", newForum.getEditedAt().toString()),
                                        Map.entry("likeCount", sharingForumService.getLikeCount(newForum)),
                                        Map.entry("isLiked", sharingForumService.isLiked(newForum, user)),
                                        Map.entry("commentCount", sharingForumService.getCommentCount(newForum)),
                                        Map.entry("comments", sharingForumService.getComments(newForum)),
                                        Map.entry("isSelfForum", newForum.getIdUser() == user));
                        } else {
                            httpCode = HTTPCode.FORBIDDEN;
                            data = new ErrorMessage(httpCode,
                                    "Konten Forum Tidak Sopan dan Mungkin Bisa Menyinggung Seseorang");
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
                data = new ErrorMessage(httpCode, "Data Forum Tidak Valid");
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
    public ResponseEntity<Object> getAllForum(HttpServletRequest request)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (authService.isSessionAlive(sessionToken)) {
                if (authService.isSessionUser(sessionToken)) {
                    User user = authService.findLoginInfoByToken(sessionToken).get().getIdUser();
                    List<SharingForum> allForums = sharingForumService.findAllForums();
                    ArrayList<Object> response = new ArrayList<Object>();
                    for (SharingForum forum : allForums) {
                        response.add(Map.ofEntries(
                                        Map.entry("id", forum.getId()),
                                        Map.entry("userId", forum.getIdUser().getId()),
                                        Map.entry("judul", forum.getJudul()),
                                        Map.entry("content", forum.getContent()),
                                        Map.entry("createdAt", forum.getCreatedAt().toString()),
                                        Map.entry("editedAt", forum.getEditedAt().toString()),
                                        Map.entry("likeCount", sharingForumService.getLikeCount(forum)),
                                        Map.entry("isLiked", sharingForumService.isLiked(forum, user)),
                                        Map.entry("commentCount", sharingForumService.getCommentCount(forum)),
                                        Map.entry("comments", sharingForumService.getComments(forum)),
                                        Map.entry("isSelfForum", forum.getIdUser() == user)));
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
                    User user = authService.findLoginInfoByToken(sessionToken).get().getIdUser();
                    Optional<SharingForum> optionalForum = sharingForumService.findForumById(forumId);
                    if (optionalForum.isPresent()) {
                        SharingForum forum = optionalForum.get();
                        data = Map.ofEntries(
                                        Map.entry("id", forum.getId()),
                                        Map.entry("userId", forum.getIdUser().getId()),
                                        Map.entry("judul", forum.getJudul()),
                                        Map.entry("content", forum.getContent()),
                                        Map.entry("createdAt", forum.getCreatedAt().toString()),
                                        Map.entry("editedAt", forum.getEditedAt().toString()),
                                        Map.entry("likeCount", sharingForumService.getLikeCount(forum)),
                                        Map.entry("isLiked", sharingForumService.isLiked(forum, user)),
                                        Map.entry("commentCount", sharingForumService.getCommentCount(forum)),
                                        Map.entry("comments", sharingForumService.getComments(forum)),
                                        Map.entry("isSelfForum", forum.getIdUser() == user));
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