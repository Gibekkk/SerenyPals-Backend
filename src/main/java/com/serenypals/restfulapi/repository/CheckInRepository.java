package com.serenypals.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serenypals.restfulapi.model.CheckIn;

public interface CheckInRepository extends JpaRepository<CheckIn, String> {
}
