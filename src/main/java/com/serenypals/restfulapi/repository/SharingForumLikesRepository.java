package com.serenypals.restfulapi.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serenypals.restfulapi.model.SharingForumLikes;
import com.serenypals.restfulapi.model.SharingForum;
import com.serenypals.restfulapi.model.User;

public interface SharingForumLikesRepository extends JpaRepository<SharingForumLikes, String> {
    Optional<SharingForumLikes> findByIdForumAndIdUser(SharingForum idForum, User idUser);
}
