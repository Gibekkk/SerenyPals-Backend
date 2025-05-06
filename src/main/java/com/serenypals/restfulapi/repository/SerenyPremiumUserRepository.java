package com.serenypals.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serenypals.restfulapi.model.SerenyPremiumUser;

public interface SerenyPremiumUserRepository extends JpaRepository<SerenyPremiumUser, String> {
}
