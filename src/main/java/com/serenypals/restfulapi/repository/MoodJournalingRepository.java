package com.serenypals.restfulapi.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serenypals.restfulapi.model.MoodJournaling;
import com.serenypals.restfulapi.model.User;

public interface MoodJournalingRepository extends JpaRepository<MoodJournaling, String> {
    List<MoodJournaling> findAllByIdUser(User user);
}
