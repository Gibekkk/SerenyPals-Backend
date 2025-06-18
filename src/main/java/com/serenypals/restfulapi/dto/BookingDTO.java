package com.serenypals.restfulapi.dto;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    private String bookingTime;
    private int sessionCount;

    public boolean checkDTO() {
        trim();
        if (this.bookingTime == null)
            throw new IllegalArgumentException("Jadwal Booking Tidak Boleh Bernilai NULL");
            boolean sessionCount = this.sessionCount > 0;
            if (!sessionCount)
                throw new IllegalArgumentException("Jumlah Sesi Tidak Valid");
        return this.bookingTime != null && sessionCount && checkJadwal();
    }

    public boolean checkJadwal() {
        boolean bookingTime = Optional.ofNullable(this.bookingTime)
                .map(s -> {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                        LocalDateTime schedule = LocalDateTime.parse(s, formatter);
                        return schedule.isAfter(LocalDateTime.now());
                    } catch (DateTimeParseException e) {
                        return false;
                    }
                })
                .orElse(false);
        if (!bookingTime)
            throw new IllegalArgumentException("Jadwal Booking Tidak Valid");
        return bookingTime;
    }

    public void trim() {
        this.bookingTime = Optional.ofNullable(this.bookingTime).map(String::trim).filter(s -> !s.isBlank())
                .orElse(null);
    }
}
