package com.serenypals.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serenypals.restfulapi.model.SerenyPremium;

public interface SerenyPremiumRepository extends JpaRepository<SerenyPremium, String> {
}
