package com.serenypals.restfulapi.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.serenypals.restfulapi.repository.MoodJournalingRepository;
import com.serenypals.restfulapi.model.MoodJournaling;
import com.serenypals.restfulapi.model.User;
import com.serenypals.restfulapi.dto.JournalDTO;

@Service
public class MoodJournalingService {
    @Autowired
    private MoodJournalingRepository moodJournalingRepository;

    public MoodJournaling createJournal(JournalDTO journalDTO, User user) {
        MoodJournaling newJournal = new MoodJournaling();
        newJournal.setIdUser(user);
        newJournal.setGangguan(journalDTO.getGangguan());
        newJournal.setCerita(journalDTO.getCerita());
        newJournal.setMoodScale(journalDTO.getSkalaMood());
        newJournal.setCreatedAt(LocalDate.now());
        return moodJournalingRepository.save(newJournal);
    }

    public Optional<MoodJournaling> getTodayJournalByUser(User user) {
        List<MoodJournaling> todayJournals = moodJournalingRepository.findAllByIdUser(user).stream()
                .filter(journal -> journal.getIdUser().equals(user))
                .filter(journal -> journal.getCreatedAt().isEqual(LocalDate.now()))
                .collect(Collectors.toList());
        if (todayJournals.size() > 0) {
            return Optional.of(todayJournals.get(0));
        }
        return Optional.empty();
    }
}