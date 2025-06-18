package com.serenypals.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serenypals.restfulapi.model.BookingPsikolog;
import com.serenypals.restfulapi.model.Psikolog;
import com.serenypals.restfulapi.model.User;
import java.util.List;

public interface BookingPsikologRepository extends JpaRepository<BookingPsikolog, String> {
    List<BookingPsikolog> findAllByIdPsikolog(Psikolog psikolog);
    List<BookingPsikolog> findAllByIdUser(User user);
}
