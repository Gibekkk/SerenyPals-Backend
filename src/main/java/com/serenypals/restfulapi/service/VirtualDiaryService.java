package com.serenypals.restfulapi.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.serenypals.restfulapi.repository.VirtualDiaryRepository;
import com.serenypals.restfulapi.model.VirtualDiary;
import com.serenypals.restfulapi.model.LoginInfo;

@Service
public class VirtualDiaryService {
    @Autowired
    private VirtualDiaryRepository virtualDiaryRepository;

    public Optional<VirtualDiary> findVirtualDiaryById(String id) {
        return virtualDiaryRepository.findById(id).filter(f -> f.getDeletedAt() == null);
    }

    public List<VirtualDiary> findAllVirtualDiariesByLoginInfo(LoginInfo loginInfo) {
        return virtualDiaryRepository.findAll().stream()
                .filter(virtualDiary -> virtualDiary.getDeletedAt() == null)
                .filter(virtualDiary -> virtualDiary.getIdUser().getIdLogin().equals(loginInfo))
                .collect(Collectors.toList());
    }
}