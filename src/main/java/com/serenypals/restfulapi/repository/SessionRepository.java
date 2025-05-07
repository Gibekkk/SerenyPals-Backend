package com.serenypals.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serenypals.restfulapi.model.Session;

public interface SessionRepository extends JpaRepository<Session, String> {
}
