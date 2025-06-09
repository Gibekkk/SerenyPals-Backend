package com.serenypals.restfulapi.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.serenypals.restfulapi.repository.BookingPsikologRepository;
import com.serenypals.restfulapi.model.BookingPsikolog;
import com.serenypals.restfulapi.model.LoginInfo;

@Service
public class BookingPsikologService {
    @Autowired
    private BookingPsikologRepository bookingPsikologRepository;

    public Optional<BookingPsikolog> findBookingById(String id) {
        return bookingPsikologRepository.findById(id).filter(f -> f.getDeletedAt() == null);
    }

    public List<BookingPsikolog> findAllBookingsByLoginInfo(LoginInfo loginInfo) {
        return bookingPsikologRepository.findAll().stream()
                .filter(booking -> booking.getDeletedAt() == null)
                .filter(booking -> booking.getIdUser().getIdLogin().equals(loginInfo))
                .collect(Collectors.toList());
    }
}