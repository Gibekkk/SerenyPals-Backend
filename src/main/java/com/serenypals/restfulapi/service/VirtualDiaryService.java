package com.serenypals.restfulapi.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.serenypals.restfulapi.repository.VirtualDiaryRepository;
import com.serenypals.restfulapi.model.VirtualDiary;
import com.serenypals.restfulapi.model.User;
import com.serenypals.restfulapi.dto.DiaryDTO;
import com.serenypals.restfulapi.util.Base64Converter;

@Service
public class VirtualDiaryService {
    @Autowired
    private VirtualDiaryRepository virtualDiaryRepository;

    @Autowired
    private Base64Converter base64Converter;

    public Optional<VirtualDiary> findVirtualDiaryById(String id) {
        return virtualDiaryRepository.findById(id).filter(f -> f.getDeletedAt() == null);
    }

    public List<VirtualDiary> findAllVirtualDiariesByUser(User user) {
        return virtualDiaryRepository.findAllByIdUser(user).stream()
                .filter(virtualDiary -> virtualDiary.getDeletedAt() == null)
                .sorted(Comparator.comparing(VirtualDiary::getCreatedAt))
                .collect(Collectors.toList());
    }

    public VirtualDiary editDiary(VirtualDiary diary, DiaryDTO diaryDTO) {
        diary.setContent(base64Converter.encrypt(diaryDTO.getContent()));
        diary.setJudul(base64Converter.encrypt(diaryDTO.getJudul()));
        diary.setEmoji(diaryDTO.getEmote());
        diary.setEditedAt(LocalDateTime.now());
        return virtualDiaryRepository.save(diary);
    }

    public VirtualDiary createDiary(DiaryDTO diaryDTO, User user) {
        VirtualDiary newDiary = new VirtualDiary();
        newDiary.setIdUser(user);
        newDiary.setContent(base64Converter.encrypt(diaryDTO.getContent()));
        newDiary.setJudul(base64Converter.encrypt(diaryDTO.getJudul()));
        newDiary.setEmoji(diaryDTO.getEmote());
        newDiary.setCreatedAt(LocalDateTime.now());
        newDiary.setEditedAt(LocalDateTime.now());
        return virtualDiaryRepository.save(newDiary);
    }

    public void deleteDiary(VirtualDiary diary) {
        diary.setDeletedAt(LocalDate.now());
        virtualDiaryRepository.save(diary);
    }

    public String decodeDiary(String text) {
        return base64Converter.decrypt(text);
    }
}