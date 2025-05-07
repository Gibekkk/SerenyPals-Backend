package com.serenypals.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serenypals.restfulapi.model.SharingForum;

public interface SharingForumRepository extends JpaRepository<SharingForum, String> {
}
