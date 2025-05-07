package com.serenypals.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serenypals.restfulapi.model.Tips;

public interface TipsRepository extends JpaRepository<Tips, String> {
}
