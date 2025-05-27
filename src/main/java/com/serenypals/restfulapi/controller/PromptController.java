package com.serenypals.restfulapi.controller;

import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import com.serenypals.restfulapi.service.PromptService;
import com.serenypals.restfulapi.model.AIChatRoom;
import com.serenypals.restfulapi.dto.PromptDTO;
import com.serenypals.restfulapi.dto.RoomNameDTO;
import com.serenypals.restfulapi.model.LoginInfo;
import com.serenypals.restfulapi.service.AuthService;
import com.serenypals.restfulapi.util.ErrorMessage;
import com.serenypals.restfulapi.util.HTTPCode;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
@RequestMapping(value = "${storage.api-prefix}/prompt")
public class PromptController {

    @Autowired
    private PromptService promptService;

    @Autowired
    private AuthService authService;

    private Object data = "";

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<Object> getChatHistory(HttpServletRequest request, @PathVariable String chatRoomId)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (authService.isSessionAlive(sessionToken)) {
                if (authService.isSessionUser(sessionToken)) {
                    LoginInfo loginInfo = authService.findLoginInfoByToken(sessionToken).get();
                    Optional<AIChatRoom> optionalRoom = promptService.findChatRoomById(chatRoomId);
                    if (optionalRoom.isPresent()) {
                        AIChatRoom chatRoom = optionalRoom.get();
                        if (chatRoom.getIdUser().getIdLogin().equals(loginInfo)) {
                            data = data = Map.of(
                                    "chatRoomId", chatRoom.getId(),
                                    "chatRoomName", chatRoom.getJudulChat(),
                                    "chatHistory", promptService.getHistory(chatRoom));
                        } else {
                            httpCode = HTTPCode.FORBIDDEN;
                            data = new ErrorMessage(httpCode, "Anda Tidak Memiliki Akses Ke Chat Room Ini");
                        }
                    } else {
                        httpCode = HTTPCode.NOT_FOUND;
                        data = new ErrorMessage(httpCode, "Chat Room Tidak Ditemukan");
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

    @DeleteMapping("/{chatRoomId}")
    public ResponseEntity<Object> deleteChat(HttpServletRequest request, @PathVariable String chatRoomId)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (authService.isSessionAlive(sessionToken)) {
                if (authService.isSessionUser(sessionToken)) {
                    LoginInfo loginInfo = authService.findLoginInfoByToken(sessionToken).get();
                    Optional<AIChatRoom> optionalRoom = promptService.findChatRoomById(chatRoomId);
                    if (optionalRoom.isPresent()) {
                        AIChatRoom chatRoom = optionalRoom.get();
                        if (chatRoom.getIdUser().getIdLogin().equals(loginInfo)) {
                            promptService.deleteChatRoomById(chatRoom);
                            data = data = Map.of("Status", "Chat Room Berhasil Dihapus");
                        } else {
                            httpCode = HTTPCode.FORBIDDEN;
                            data = new ErrorMessage(httpCode, "Anda Tidak Memiliki Akses Ke Chat Room Ini");
                        }
                    } else {
                        httpCode = HTTPCode.NOT_FOUND;
                        data = new ErrorMessage(httpCode, "Chat Room Tidak Ditemukan");
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

    @GetMapping
    public ResponseEntity<Object> getChatRooms(HttpServletRequest request)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (authService.isSessionAlive(sessionToken)) {
                if (authService.isSessionUser(sessionToken)) {
                    LoginInfo loginInfo = authService.findLoginInfoByToken(sessionToken).get();
                    List<AIChatRoom> listRoom = promptService.getChatRoomsByLoginInfo(loginInfo);
                    ArrayList<Object> chatRoomsData = new ArrayList<Object>();
                    for (AIChatRoom chatRoom : listRoom) {
                        chatRoomsData.add(Map.of(
                                "chatRoomId", chatRoom.getId(),
                                "chatRoomName", chatRoom.getJudulChat(),
                                "lastChatDateTime", chatRoom.getLastChatDateTime(),
                                "lastChat", chatRoom.getLastChat().getChat(),
                                "lastChatIsBot", chatRoom.getLastChat().getIsBot()));
                    }
                    data = Map.of(
                            "chatRooms", chatRoomsData);
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

    @PostMapping("/{chatRoomId}")
    public ResponseEntity<Object> sendPrompt(HttpServletRequest request, @PathVariable String chatRoomId,
            @RequestBody PromptDTO promptDTO)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (promptDTO.checkDTO()) {
                if (authService.isSessionAlive(sessionToken)) {
                    if (authService.isSessionUser(sessionToken)) {
                        LoginInfo loginInfo = authService.findLoginInfoByToken(sessionToken).get();
                        Optional<AIChatRoom> optionalRoom = promptService.findChatRoomById(chatRoomId);
                        if (optionalRoom.isPresent()) {
                            AIChatRoom chatRoom = optionalRoom.get();
                            if (chatRoom.getIdUser().getIdLogin().equals(loginInfo)) {
                                data = Map.of(
                                        "chatRoomId", chatRoom.getId(),
                                        "chatRoomName", chatRoom.getJudulChat(),
                                        "response", promptService.sendPrompt(promptDTO.getPrompt(), chatRoom));
                            } else {
                                httpCode = HTTPCode.FORBIDDEN;
                                data = new ErrorMessage(httpCode, "Anda Tidak Memiliki Akses Ke Chat Room Ini");
                            }
                        } else {
                            httpCode = HTTPCode.NOT_FOUND;
                            data = new ErrorMessage(httpCode, "Chat Room Tidak Ditemukan");
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
                data = new ErrorMessage(httpCode, "Prompt Data Tidak Valid");
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

    @PatchMapping("/{chatRoomId}")
    public ResponseEntity<Object> renameRoom(HttpServletRequest request, @PathVariable String chatRoomId,
            @RequestBody RoomNameDTO roomNameDTO)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (roomNameDTO.checkDTO()) {
                if (authService.isSessionAlive(sessionToken)) {
                    if (authService.isSessionUser(sessionToken)) {
                        LoginInfo loginInfo = authService.findLoginInfoByToken(sessionToken).get();
                        Optional<AIChatRoom> optionalRoom = promptService.findChatRoomById(chatRoomId);
                        if (optionalRoom.isPresent()) {
                            AIChatRoom chatRoom = optionalRoom.get();
                            if (chatRoom.getIdUser().getIdLogin().equals(loginInfo)) {
                                data = Map.of(
                                        "chatRoomId", chatRoom.getId(),
                                        "chatRoomName", promptService.renameChatRoom(chatRoom,
                                                roomNameDTO.getRoomName()));
                            } else {
                                httpCode = HTTPCode.FORBIDDEN;
                                data = new ErrorMessage(httpCode, "Anda Tidak Memiliki Akses Ke Chat Room Ini");
                            }
                        } else {
                            httpCode = HTTPCode.NOT_FOUND;
                            data = new ErrorMessage(httpCode, "Chat Room Tidak Ditemukan");
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
                data = new ErrorMessage(httpCode, "Judul Chat Tidak Valid");
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
    public ResponseEntity<Object> createRoomPrompt(HttpServletRequest request, @RequestBody PromptDTO promptDTO)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (authService.isSessionAlive(sessionToken)) {
                if (authService.isSessionUser(sessionToken)) {
                    LoginInfo loginInfo = authService.findLoginInfoByToken(sessionToken).get();
                    AIChatRoom chatRoom = promptService.createChatRoom(loginInfo);
                    String response = promptService.sendPrompt(promptDTO.getPrompt(), chatRoom);
                    String chatRoomName = promptService.renameChatRoom(chatRoom,
                            promptService.generateRoomName(chatRoom));
                    data = Map.of(
                            "chatRoomId", chatRoom.getId(),
                            "chatRoomName", chatRoomName,
                            "response", response);
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