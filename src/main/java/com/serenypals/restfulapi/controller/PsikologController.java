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

import com.serenypals.restfulapi.service.PsikologService;
import com.serenypals.restfulapi.model.Psikolog;
import com.serenypals.restfulapi.model.BookingPsikolog;
import com.serenypals.restfulapi.model.PsikologChatRoom;
import com.serenypals.restfulapi.dto.BookingDTO;
import com.serenypals.restfulapi.model.User;
import com.serenypals.restfulapi.model.LoginInfo;
import com.serenypals.restfulapi.service.AuthService;
import com.serenypals.restfulapi.util.ErrorMessage;
import com.serenypals.restfulapi.util.HTTPCode;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
@RequestMapping(value = "${storage.api-prefix}/psikolog")
public class PsikologController {

    @Autowired
    private PsikologService psikologService;

    @Autowired
    private AuthService authService;

    private Object data = "";

    @GetMapping("/chatRoom/{chatRoomId}")
    public ResponseEntity<Object> getChatRoomById(HttpServletRequest request, @PathVariable String chatRoomId)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (authService.isSessionAlive(sessionToken)) {
                if (authService.isSessionUser(sessionToken)) {
                    User user = authService.findLoginInfoByToken(sessionToken).get().getIdUser();
                    Optional<PsikologChatRoom> roomOptional = psikologService.getChatRoomByUserAndId(user, chatRoomId);
                    if (roomOptional.isPresent()) {
                        PsikologChatRoom chatRoom = roomOptional.get();
                        data = Map.of(
                                "chatRoomId", chatRoom.getId(),
                                "psikologId", chatRoom.getIdPsikolog().getId(),
                                "userId", chatRoom.getIdUser().getId(),
                                "psikologName", chatRoom.getIdPsikolog().getNama(),
                                "lastChatDateTime", chatRoom.getLastChatDateTime(),
                                "lastChat",
                                Optional.ofNullable(chatRoom.getLastChat()).map(chat -> chat.getChat()).orElse(""),
                                "lastChatIsPsikolog", Optional.ofNullable(chatRoom.getLastChat())
                                        .map(chat -> chat.getIsPsikolog()).orElse(false));
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

    @GetMapping("/chatRoom")
    public ResponseEntity<Object> getChatRooms(HttpServletRequest request)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (authService.isSessionAlive(sessionToken)) {
                if (authService.isSessionUser(sessionToken)) {
                    User user = authService.findLoginInfoByToken(sessionToken).get().getIdUser();
                    List<PsikologChatRoom> listRoom = psikologService.getChatRoomsByUser(user);
                    ArrayList<Object> chatRoomsData = new ArrayList<Object>();
                    for (PsikologChatRoom chatRoom : listRoom) {
                        chatRoomsData.add(Map.of(
                                "chatRoomId", chatRoom.getId(),
                                "psikologId", chatRoom.getIdPsikolog().getId(),
                                "userId", chatRoom.getIdUser().getId(),
                                "psikologName", chatRoom.getIdPsikolog().getNama(),
                                "lastChatDateTime", chatRoom.getLastChatDateTime(),
                                "lastChat",
                                Optional.ofNullable(chatRoom.getLastChat()).map(chat -> chat.getChat()).orElse(""),
                                "lastChatIsPsikolog", Optional.ofNullable(chatRoom.getLastChat())
                                        .map(chat -> chat.getIsPsikolog()).orElse(false)));
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

    @PatchMapping("/booking/{bookingId}")
    public ResponseEntity<Object> editBookingById(HttpServletRequest request, @PathVariable String bookingId,
            @RequestBody BookingDTO bookingDTO)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (bookingDTO.checkDTO()) {
                if (authService.isSessionAlive(sessionToken)) {
                    if (authService.isSessionUser(sessionToken)) {
                        User user = authService.findLoginInfoByToken(sessionToken).get().getIdUser();
                        Optional<BookingPsikolog> bookingOptional = psikologService.findBookingById(bookingId);
                        if (bookingOptional.isPresent()) {
                            BookingPsikolog booking = bookingOptional.get();
                            Optional<BookingPsikolog> optionalCurrentBooking = psikologService
                                    .findCurrentBookingByUser(user);
                            if (optionalCurrentBooking.isPresent() ? !optionalCurrentBooking.get().equals(booking)
                                    : true) {
                                if (booking.getIdUser().equals(user)) {
                                    psikologService.editBooking(booking, bookingDTO);
                                    data = Map.of(
                                            "id", booking.getId(),
                                            "startAt", booking.getStartAt(),
                                            "endAt",
                                            booking.getStartAt()
                                                    .plusMinutes(
                                                            psikologService.getMinutesPerSession()
                                                                    * booking.getJumlahSesi()),
                                            "createdAt", booking.getCreatedAt(),
                                            "editedAt", booking.getEditedAt(),
                                            "userId", booking.getIdUser().getId(),
                                            "psikologId", booking.getIdPsikolog().getId(),
                                            "chatRoomId", psikologService.findByBooking(booking).getId());
                                } else {
                                    httpCode = HTTPCode.FORBIDDEN;
                                    data = new ErrorMessage(httpCode, "Bukan Bookingan Anda");
                                }
                            } else {
                                httpCode = HTTPCode.FORBIDDEN;
                                data = new ErrorMessage(httpCode, "Tidak Bisa Mengedit Bookingan Yang Sedang Berjalan");
                            }
                        } else {
                            httpCode = HTTPCode.NOT_FOUND;
                            data = new ErrorMessage(httpCode, "Bookingan Tidak Ditemukan");
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
                data = new ErrorMessage(httpCode, "Data Booking Tidak Valid");
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

    @DeleteMapping("/booking/{bookingId}")
    public ResponseEntity<Object> deleteBookingById(HttpServletRequest request, @PathVariable String bookingId)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (authService.isSessionAlive(sessionToken)) {
                if (authService.isSessionUser(sessionToken)) {
                    User user = authService.findLoginInfoByToken(sessionToken).get().getIdUser();
                    Optional<BookingPsikolog> bookingOptional = psikologService.findBookingById(bookingId);
                    if (bookingOptional.isPresent()) {
                        BookingPsikolog booking = bookingOptional.get();
                        if (booking.getIdUser().equals(user)) {
                            Optional<BookingPsikolog> optionalCurrentBooking = psikologService
                                    .findCurrentBookingByUser(user);
                            if (optionalCurrentBooking.isPresent() ? !optionalCurrentBooking.get().equals(booking)
                                    : true) {
                                psikologService.deleteBooking(booking);
                                data = Map.of(
                                        "Status", "Booking Dihapus");
                            } else {
                                httpCode = HTTPCode.FORBIDDEN;
                                data = new ErrorMessage(httpCode, "Bukan Bookingan Anda");
                            }
                        } else {
                            httpCode = HTTPCode.FORBIDDEN;
                            data = new ErrorMessage(httpCode, "Tidak Bisa Menghapus Bookingan Yang Sedang Berjalan");
                        }
                    } else {
                        httpCode = HTTPCode.NOT_FOUND;
                        data = new ErrorMessage(httpCode, "Bookingan Tidak Ditemukan");
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

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<Object> getBookingById(HttpServletRequest request, @PathVariable String bookingId)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (authService.isSessionAlive(sessionToken)) {
                if (authService.isSessionUser(sessionToken) || authService.isSessionPsikolog(sessionToken)) {
                    Optional<BookingPsikolog> bookingOptional = psikologService.findBookingById(bookingId);
                    if (bookingOptional.isPresent()) {
                        BookingPsikolog booking = bookingOptional.get();
                        data = Map.of(
                                "id", booking.getId(),
                                "startAt", booking.getStartAt(),
                                "endAt",
                                booking.getStartAt()
                                        .plusMinutes(psikologService.getMinutesPerSession() * booking.getJumlahSesi()),
                                "createdAt", booking.getCreatedAt(),
                                "editedAt", booking.getEditedAt(),
                                "userId", booking.getIdUser().getId(),
                                "psikologId", booking.getIdPsikolog().getId(),
                                "chatRoomId", psikologService.findByBooking(booking).getId());
                    } else {
                        httpCode = HTTPCode.NOT_FOUND;
                        data = new ErrorMessage(httpCode, "Bookingan Tidak Ditemukan");
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

    @GetMapping("/booking")
    public ResponseEntity<Object> getBookings(HttpServletRequest request)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (authService.isSessionAlive(sessionToken)) {
                if (authService.isSessionUser(sessionToken) || authService.isSessionPsikolog(sessionToken)) {
                    LoginInfo loginInfo = authService.findLoginInfoByToken(sessionToken).get();
                    data = authService.isSessionUser(sessionToken)
                            ? psikologService.getBookingsByUser(loginInfo.getIdUser())
                            : psikologService.getBookingsByPsikolog(loginInfo.getIdPsikolog());
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

    @GetMapping("/booking/current")
    public ResponseEntity<Object> getCurrentBooking(HttpServletRequest request)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (authService.isSessionAlive(sessionToken)) {
                if (authService.isSessionUser(sessionToken) || authService.isSessionPsikolog(sessionToken)) {
                    LoginInfo loginInfo = authService.findLoginInfoByToken(sessionToken).get();
                    Optional<BookingPsikolog> bookingOptional = authService.isSessionUser(sessionToken)
                            ? psikologService.findCurrentBookingByUser(loginInfo.getIdUser())
                            : psikologService.findCurrentBookingByPsikolog(loginInfo.getIdPsikolog());
                    if (bookingOptional.isPresent()) {
                        BookingPsikolog booking = bookingOptional.get();
                        data = Map.of(
                                "id", booking.getId(),
                                "startAt", booking.getStartAt(),
                                "endAt",
                                booking.getStartAt()
                                        .plusMinutes(psikologService.getMinutesPerSession() * booking.getJumlahSesi()),
                                "createdAt", booking.getCreatedAt(),
                                "editedAt", booking.getEditedAt(),
                                "userId", booking.getIdUser().getId(),
                                "psikologId", booking.getIdPsikolog().getId(),
                                "chatRoomId", psikologService.findByBooking(booking).getId());
                    } else {
                        httpCode = HTTPCode.NOT_FOUND;
                        data = new ErrorMessage(httpCode, "Tidak Ada Bookingan Berjalan");
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

    @GetMapping("/{psikologId}")
    public ResponseEntity<Object> getPsikologById(HttpServletRequest request, @PathVariable String psikologId)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (authService.isSessionAlive(sessionToken)) {
                if (authService.isSessionUser(sessionToken)) {
                    Optional<Psikolog> psikologOptional = psikologService.findPsikologById(psikologId);
                    if (psikologOptional.isPresent()) {
                        Psikolog psikolog = psikologOptional.get();
                        data = Map.ofEntries(
                                Map.entry("id", psikolog.getId()),
                                Map.entry("nama", psikolog.getNama()),
                                Map.entry("nomorTelepon", psikolog.getNomorTelepon()),
                                Map.entry("jadwal", psikologService.getBookingsByPsikolog(psikolog)));
                    } else {
                        httpCode = HTTPCode.NOT_FOUND;
                        data = new ErrorMessage(httpCode, "Psikolog Tidak Ditemukan");
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

    @PostMapping("/{psikologId}/booking")
    public ResponseEntity<Object> bookPsikologById(HttpServletRequest request, @PathVariable String psikologId,
            @RequestBody BookingDTO bookingDTO)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (bookingDTO.checkDTO()) {
                if (authService.isSessionAlive(sessionToken)) {
                    if (authService.isSessionUser(sessionToken)) {
                        User user = authService.findLoginInfoByToken(sessionToken).get().getIdUser();
                        Optional<Psikolog> psikologOptional = psikologService.findPsikologById(psikologId);
                        if (psikologOptional.isPresent()) {
                            Psikolog psikolog = psikologOptional.get();
                            if (psikologService.isPsikologAvailable(psikolog, bookingDTO)) {
                                if (psikologService.isUserAvailable(user, bookingDTO)) {
                                    psikologService.createNewBooking(psikolog, user, bookingDTO);
                                    data = Map.ofEntries(
                                            Map.entry("id", psikolog.getId()),
                                            Map.entry("nama", psikolog.getNama()),
                                            Map.entry("nomorTelepon", psikolog.getNomorTelepon()),
                                            Map.entry("jadwal", psikologService.getBookingsByPsikolog(psikolog)));
                                } else {
                                    httpCode = HTTPCode.BAD_REQUEST;
                                    data = new ErrorMessage(httpCode, "Jadwal Anda Tidak Tersedia");
                                }
                            } else {
                                httpCode = HTTPCode.BAD_REQUEST;
                                data = new ErrorMessage(httpCode, "Jadwal Psikolog Tidak Tersedia");
                            }
                        } else {
                            httpCode = HTTPCode.NOT_FOUND;
                            data = new ErrorMessage(httpCode, "Psikolog Tidak Ditemukan");
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
                data = new ErrorMessage(httpCode, "Data Booking Tidak Valid");
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
    public ResponseEntity<Object> getPsikolog(HttpServletRequest request)
            throws Exception {
        String sessionToken = request.getHeader("Token");
        HTTPCode httpCode = HTTPCode.OK;
        try {
            if (authService.isSessionAlive(sessionToken)) {
                if (authService.isSessionUser(sessionToken)) {
                    ArrayList<Object> response = new ArrayList<Object>();
                    for (Psikolog psikolog : psikologService.findAllPsikolog()) {
                        response.add(Map.ofEntries(
                                Map.entry("id", psikolog.getId()),
                                Map.entry("nama", psikolog.getNama()),
                                Map.entry("nomorTelepon", psikolog.getNomorTelepon()),
                                Map.entry("jadwal", psikologService.getBookingsByPsikolog(psikolog))));
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
}