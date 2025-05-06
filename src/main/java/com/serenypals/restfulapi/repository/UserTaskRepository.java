package com.serenypals.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serenypals.restfulapi.model.UserTask;

public interface UserTaskRepository extends JpaRepository<UserTask, String> {
}
