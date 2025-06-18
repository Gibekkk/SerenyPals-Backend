package com.serenypals.restfulapi.service;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.serenypals.restfulapi.repository.PsikologChatRoomRepository;
import com.serenypals.restfulapi.repository.PsikologRepository;
import com.serenypals.restfulapi.repository.BookingPsikologRepository;
import com.serenypals.restfulapi.model.PsikologChatRoom;
import com.serenypals.restfulapi.repository.PsikologChatRepository;
import com.serenypals.restfulapi.dto.BookingDTO;
import com.serenypals.restfulapi.dto.ChatDTO;
import com.serenypals.restfulapi.model.Psikolog;
import com.serenypals.restfulapi.model.PsikologChat;
import com.serenypals.restfulapi.model.BookingPsikolog;
import com.serenypals.restfulapi.model.User;

@Service
public class PsikologService {
    @Autowired
    private PsikologChatRoomRepository psikologChatRoomRepository;

    @Autowired
    private PsikologRepository psikologRepository;

    @Autowired
    private PsikologChatRepository psikologChatRepository;

    @Autowired
    private BookingPsikologRepository bookingPsikologRepository;

    final private int minutesPerSession = 50;
    final private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public int getMinutesPerSession() {
        return minutesPerSession;
    }

    public List<Psikolog> findAllPsikolog() {
        return psikologRepository.findAll().stream()
                .filter(psikolog -> psikolog.getIdLogin().getDeletedAt() == null)
                .collect(Collectors.toList());
    }

    public Optional<Psikolog> findPsikologById(String psikologId) {
        Optional<Psikolog> psikologOptional = psikologRepository.findById(psikologId);
        if (psikologOptional.isPresent()) {
            Psikolog psikolog = psikologOptional.get();
            return psikolog.getIdLogin().getDeletedAt() == null ? psikologOptional : Optional.empty();
        }
        return Optional.empty();
    }

    public Optional<BookingPsikolog> findBookingById(String bookingId) {
        Optional<BookingPsikolog> bookingOptional = bookingPsikologRepository.findById(bookingId);
        if (bookingOptional.isPresent()) {
            BookingPsikolog booking = bookingOptional.get();
            return booking.getDeletedAt() == null ? bookingOptional : Optional.empty();
        }
        return Optional.empty();
    }

    public PsikologChatRoom createChatRoom(Psikolog psikolog, User user) {
        PsikologChatRoom psikologChatRoom = new PsikologChatRoom();
        psikologChatRoom.setIdPsikolog(psikolog);
        psikologChatRoom.setIdUser(user);
        psikologChatRoom.setCreatedAt(LocalDateTime.now());
        psikologChatRoom.setEditedAt(LocalDateTime.now());
        return psikologChatRoomRepository.save(psikologChatRoom);
    }

    public PsikologChatRoom findChatRoomByBooking(BookingPsikolog booking) {
        return findChatRoomByUserAndPsikolog(booking.getIdPsikolog(), booking.getIdUser()).get();
    }

    public PsikologChat sendChatUser(PsikologChatRoom chatRoom, ChatDTO chatDTO) {
        PsikologChat newChat = new PsikologChat();
        newChat.setChat(chatDTO.getChat());
        newChat.setIdChatRoom(chatRoom);
        newChat.setIsPsikolog(false);
        newChat.setCreatedAt(LocalDateTime.now());
        return psikologChatRepository.save(newChat);
    }

    public PsikologChat sendChatPsikolog(PsikologChatRoom chatRoom, ChatDTO chatDTO) {
        PsikologChat newChat = new PsikologChat();
        newChat.setChat(chatDTO.getChat());
        newChat.setIdChatRoom(chatRoom);
        newChat.setIsPsikolog(true);
        newChat.setCreatedAt(LocalDateTime.now());
        return psikologChatRepository.save(newChat);
    }

    public void seeChatFromUser(PsikologChatRoom chatRoom) {
        for (PsikologChat chat : chatRoom.getChats()) {
            if (chat.getIsPsikolog() && chat.getSeenAt() == null) {
                chat.setSeenAt(LocalDateTime.now());
                psikologChatRepository.save(chat);
            }
        }
    }

    public ArrayList<Object> getChatsFromChatRoom(PsikologChatRoom chatRoom) {
        ArrayList<Object> result = new ArrayList<Object>();
        for (PsikologChat chat : chatRoom.getChats()) {
            result.add(Map.of(
                    "id", chat.getId(),
                    "chat", chat.getChat(),
                    "isPsikolog", chat.getIsPsikolog(),
                    "createdAt", chat.getCreatedAt().toString(),
                    "seenAt", Optional.ofNullable(chat.getSeenAt()).map(seenAt -> seenAt.toString()).orElse("")));
        }
        return result;
    }

    public void seeChatFromPsikolog(PsikologChatRoom chatRoom) {
        for (PsikologChat chat : chatRoom.getChats()) {
            if (!chat.getIsPsikolog() && chat.getSeenAt() == null) {
                chat.setSeenAt(LocalDateTime.now());
                psikologChatRepository.save(chat);
            }
        }
    }

    public List<PsikologChatRoom> getChatRoomsByUser(User user) {
        return psikologChatRoomRepository.findAll().stream()
                .filter(chatRoom -> chatRoom.getDeletedAt() == null)
                .filter(chatRoom -> chatRoom.getIdUser().equals(user))
                .sorted(Comparator.comparing(PsikologChatRoom::getLastChatDateTime).reversed())
                .collect(Collectors.toList());
    }

    public Optional<PsikologChatRoom> getChatRoomByUserAndId(User user, String id) {
        for (PsikologChatRoom chatRoom : getChatRoomsByUser(user)) {
            if (chatRoom.getId().equals(id))
                return Optional.of(chatRoom);
        }
        return Optional.empty();
    }

    public List<PsikologChatRoom> getChatRoomsByPsikolog(Psikolog psikolog) {
        return psikologChatRoomRepository.findAll().stream()
                .filter(chatRoom -> chatRoom.getDeletedAt() == null)
                .filter(chatRoom -> chatRoom.getIdPsikolog().equals(psikolog))
                .sorted(Comparator.comparing(PsikologChatRoom::getLastChatDateTime).reversed())
                .collect(Collectors.toList());
    }

    public Optional<PsikologChatRoom> getChatRoomByPsikologAndId(Psikolog psikolog, String id) {
        for (PsikologChatRoom chatRoom : getChatRoomsByPsikolog(psikolog)) {
            if (chatRoom.getId().equals(id))
                return Optional.of(chatRoom);
        }
        return Optional.empty();
    }

    public Optional<PsikologChatRoom> findChatRoomByUserAndPsikolog(Psikolog psikolog, User user) {
        return psikologChatRoomRepository.findByIdPsikologAndIdUser(psikolog, user);
    }

    public Boolean chatRoomExistByUserAndPsikolog(Psikolog psikolog, User user) {
        return findChatRoomByUserAndPsikolog(psikolog, user).isPresent();
    }

    public BookingPsikolog createNewBooking(Psikolog psikolog, User user, BookingDTO bookingDTO) {
        if (!chatRoomExistByUserAndPsikolog(psikolog, user)) {
            createChatRoom(psikolog, user);
        }
        BookingPsikolog newBooking = new BookingPsikolog();
        newBooking.setIdPsikolog(psikolog);
        newBooking.setIdUser(user);
        newBooking.setStartAt(LocalDateTime.parse(bookingDTO.getBookingTime(), formatter));
        newBooking.setJumlahSesi(bookingDTO.getSessionCount());
        newBooking.setCreatedAt(LocalDateTime.now());
        newBooking.setEditedAt(LocalDateTime.now());
        return bookingPsikologRepository.save(newBooking);
    }

    public BookingPsikolog editBooking(BookingPsikolog booking, BookingDTO bookingDTO) {
        booking.setStartAt(LocalDateTime.parse(bookingDTO.getBookingTime(), formatter));
        booking.setJumlahSesi(bookingDTO.getSessionCount());
        booking.setEditedAt(LocalDateTime.now());
        return bookingPsikologRepository.save(booking);
    }

    public void deleteBooking(BookingPsikolog booking) {
        booking.setDeletedAt(LocalDate.now());
        bookingPsikologRepository.save(booking);
    }

    public ArrayList<Object> getBookingsByPsikolog(Psikolog psikolog) {
        ArrayList<Object> result = new ArrayList<Object>();
        for (BookingPsikolog booking : findAllBookingByPsikolog(psikolog)) {
            result.add(Map.of(
                    "id", booking.getId(),
                    "startAt", booking.getStartAt(),
                    "endAt", booking.getStartAt().plusMinutes(minutesPerSession * booking.getJumlahSesi()),
                    "createdAt", booking.getCreatedAt(),
                    "editedAt", booking.getEditedAt(),
                    "psikologId", booking.getIdPsikolog().getId(),
                    "userId", booking.getIdUser().getId(),
                    "chatRoomId", findChatRoomByBooking(booking).getId()));
        }
        return result;
    }

    public ArrayList<Object> getBookingsByUser(User user) {
        ArrayList<Object> result = new ArrayList<Object>();
        for (BookingPsikolog booking : findAllBookingByUser(user)) {
            result.add(Map.of(
                    "id", booking.getId(),
                    "startAt", booking.getStartAt(),
                    "endAt", booking.getStartAt().plusMinutes(minutesPerSession * booking.getJumlahSesi()),
                    "createdAt", booking.getCreatedAt(),
                    "editedAt", booking.getEditedAt(),
                    "psikologId", booking.getIdPsikolog().getId(),
                    "userId", booking.getIdUser().getId(),
                    "chatRoomId", findChatRoomByBooking(booking).getId()));
        }
        return result;
    }

    public List<BookingPsikolog> findAllBookingByPsikolog(Psikolog psikolog) {
        return bookingPsikologRepository.findAllByIdPsikolog(psikolog).stream()
                .filter(booking -> booking.getDeletedAt() == null)
                .collect(Collectors.toList());
    }

    public List<BookingPsikolog> findAllBookingByUser(User user) {
        return bookingPsikologRepository.findAllByIdUser(user).stream()
                .filter(booking -> booking.getDeletedAt() == null)
                .collect(Collectors.toList());
    }

    public Optional<BookingPsikolog> findCurrentBookingByPsikolog(Psikolog psikolog) {
        List<BookingPsikolog> nearestBookings = findAllBookingByPsikolog(psikolog).stream()
                .filter(booking -> booking.getStartAt().isBefore(LocalDateTime.now()) && booking.getStartAt()
                        .plusMinutes(minutesPerSession * booking.getJumlahSesi()).isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
        return nearestBookings.size() > 0 ? Optional.of(nearestBookings.get(0)) : Optional.empty();
    }

    public Optional<BookingPsikolog> findCurrentBookingByUser(User user) {
        List<BookingPsikolog> nearestBookings = findAllBookingByUser(user).stream()
                .filter(booking -> booking.getStartAt().isBefore(LocalDateTime.now()) && booking.getStartAt()
                        .plusMinutes(minutesPerSession * booking.getJumlahSesi()).isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
        return nearestBookings.size() > 0 ? Optional.of(nearestBookings.get(0)) : Optional.empty();
    }

    public Boolean isPsikologAvailable(Psikolog psikolog, BookingDTO bookingDTO) {
        LocalDateTime startAt = LocalDateTime.parse(bookingDTO.getBookingTime(), formatter);
        LocalDateTime endAt = startAt.plusMinutes(minutesPerSession * bookingDTO.getSessionCount());
        return findAllBookingByPsikolog(psikolog).stream()
                .filter(booking -> !(booking.getStartAt().isAfter(endAt) || booking.getStartAt()
                        .plusMinutes(minutesPerSession * booking.getJumlahSesi()).isBefore(startAt)))
                .collect(Collectors.toList()).size() == 0;
    }

    public Boolean isUserAvailable(User user, BookingDTO bookingDTO) {
        LocalDateTime startAt = LocalDateTime.parse(bookingDTO.getBookingTime(), formatter);
        LocalDateTime endAt = startAt.plusMinutes(minutesPerSession * bookingDTO.getSessionCount());
        return findAllBookingByUser(user).stream()
                .filter(booking -> !(booking.getStartAt().isAfter(endAt) || booking.getStartAt()
                        .plusMinutes(minutesPerSession * booking.getJumlahSesi()).isBefore(startAt)))
                .collect(Collectors.toList()).size() == 0;
    }
}