package com.serenypals.restfulapi.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.serenypals.restfulapi.repository.SharingForumRepository;
import com.serenypals.restfulapi.repository.SharingForumCommentsRepository;
import com.serenypals.restfulapi.model.SharingForum;
import com.serenypals.restfulapi.model.SharingForumComments;
import com.serenypals.restfulapi.model.SharingForumLikes;
import com.serenypals.restfulapi.model.LoginInfo;

@Service
public class SharingForumService {
    @Autowired
    private SharingForumRepository sharingForumRepository;

    @Autowired
    private SharingForumCommentsRepository sharingForumCommentsRepository;

    public Optional<SharingForum> findForumById(String id) {
        return sharingForumRepository.findById(id).filter(f -> f.getDeletedAt() == null);
    }

    public List<SharingForum> findAllForumsByLoginInfo(LoginInfo loginInfo) {
        return sharingForumRepository.findAll().stream()
                .filter(forum -> forum.getDeletedAt() == null)
                .filter(forum -> forum.getIdUser().getIdLogin().equals(loginInfo))
                .collect(Collectors.toList());
    }

    public List<SharingForum> findAllForums() {
        return sharingForumRepository.findAll().stream()
                .filter(forum -> forum.getDeletedAt() == null)
                .collect(Collectors.toList());
    }

    public Optional<SharingForumComments> findForumCommentsById(String id) {
        return sharingForumCommentsRepository.findById(id).filter(f -> f.getDeletedAt() == null);
    }

    public List<SharingForumComments> findAllForumCommentsByLoginInfo(LoginInfo loginInfo) {
        return sharingForumCommentsRepository.findAll().stream()
                .filter(comment -> comment.getDeletedAt() == null)
                .filter(comment -> comment.getIdUser().getIdLogin().equals(loginInfo))
                .collect(Collectors.toList());
    }
}