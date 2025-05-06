package com.serenypals.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serenypals.restfulapi.model.Task;

public interface TaskRepositoryRepository extends JpaRepository<Task, String> {
}
