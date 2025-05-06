package com.serenypals.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serenypals.restfulapi.model.VirtualDiary;

public interface VirtualDiaryRepository extends JpaRepository<VirtualDiary, String> {
}
