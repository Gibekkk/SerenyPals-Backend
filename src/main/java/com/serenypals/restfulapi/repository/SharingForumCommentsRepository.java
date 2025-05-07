package com.serenypals.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serenypals.restfulapi.model.SharingForumComments;

public interface SharingForumCommentsRepository extends JpaRepository<SharingForumComments, String> {
}
