package com.serenypals.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serenypals.restfulapi.model.MoodJournaling;

public interface MoodJournalingRepository extends JpaRepository<MoodJournaling, String> {
}
