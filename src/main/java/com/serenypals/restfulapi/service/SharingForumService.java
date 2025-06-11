package com.serenypals.restfulapi.service;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.serenypals.restfulapi.repository.SharingForumRepository;
import com.serenypals.restfulapi.repository.SharingForumCommentsRepository;
import com.serenypals.restfulapi.model.SharingForum;
import com.serenypals.restfulapi.model.SharingForumComments;
import com.serenypals.restfulapi.model.SharingForumLikes;
import com.serenypals.restfulapi.model.LoginInfo;
import com.serenypals.restfulapi.model.User;
import com.serenypals.restfulapi.dto.ForumDTO;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class SharingForumService {
    @Autowired
    private SharingForumRepository sharingForumRepository;

    @Autowired
    private SharingForumCommentsRepository sharingForumCommentsRepository;

    @Autowired
    private PromptService promptService;

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

    public boolean isContentSafe(String content) throws JsonProcessingException {
        return promptService.checkPoliteness(content);
    }

    public SharingForum createForum(ForumDTO forumDTO, User user) {
        SharingForum newForum = new SharingForum();
        newForum.setJudul(forumDTO.getJudul());
        newForum.setContent(forumDTO.getContent());
        newForum.setCreatedAt(LocalDateTime.now());
        newForum.setEditedAt(LocalDateTime.now());
        newForum.setIdUser(user);
        return sharingForumRepository.save(newForum);
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