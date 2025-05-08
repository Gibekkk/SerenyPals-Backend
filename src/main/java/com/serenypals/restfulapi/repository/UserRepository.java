package com.serenypals.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serenypals.restfulapi.model.User;

public interface UserRepository extends JpaRepository<User, String> {
}
