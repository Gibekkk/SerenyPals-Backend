package com.serenypals.restfulapi.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.serenypals.restfulapi.repository.TipsRepository;
import com.serenypals.restfulapi.model.Tips;

@Service
public class TipsService {
    @Autowired
    private TipsRepository tipsRepository;

    public Optional<Tips> findTipById(String id) {
        return tipsRepository.findById(id).filter(f -> f.getDeletedAt() == null);
    }

    public List<Tips> findAllTips() {
        return tipsRepository.findAll().stream()
                .filter(tip -> tip.getDeletedAt() == null)
                .collect(Collectors.toList());
    }
}