package com.serenypals.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serenypals.restfulapi.model.SharingForumLikes;

public interface SharingForumLikesRepository extends JpaRepository<SharingForumLikes, String> {
}
