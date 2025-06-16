package com.serenypals.restfulapi.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serenypals.restfulapi.model.SharingForumComments;
import com.serenypals.restfulapi.model.SharingForum;

public interface SharingForumCommentsRepository extends JpaRepository<SharingForumComments, String> {
    List<SharingForumComments> findAllByIdForumAndDeletedAtIsNull(SharingForum idForum);
}
