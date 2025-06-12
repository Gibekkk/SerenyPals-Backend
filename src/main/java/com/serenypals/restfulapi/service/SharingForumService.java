package com.serenypals.restfulapi.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.serenypals.restfulapi.repository.SharingForumRepository;
import com.serenypals.restfulapi.repository.SharingForumCommentsRepository;
import com.serenypals.restfulapi.repository.SharingForumLikesRepository;
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
    private SharingForumLikesRepository sharingForumLikesRepository;

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
        return promptService.isPolite(content);
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

    public Optional<SharingForumLikes> findByForumAndUser(SharingForum forum, User user) {
        return sharingForumLikesRepository.findByIdForumAndIdUser(forum, user);
    }

    public boolean isLiked(SharingForum forum, User user) {
        Optional<SharingForumLikes> sharingForumLike = findByForumAndUser(forum, user);
        if(sharingForumLike.isPresent()) {
            return sharingForumLike.get().getIsDeleted() ? false : true;
        }
        return false;
    }

    public SharingForum toggleLikeForum(SharingForum forum, User user) {
        SharingForumLikes sharingForumLike = new SharingForumLikes();
        Optional<SharingForumLikes> optionalForumLikes = findByForumAndUser(forum, user);
        if (optionalForumLikes.isPresent()) {
            sharingForumLike = optionalForumLikes.get();
            sharingForumLike.setIsDeleted(!sharingForumLike.getIsDeleted());
        } else {
            sharingForumLike.setIdUser(user);
            sharingForumLike.setIdForum(forum);
            sharingForumLike.setIsDeleted(false);
        }
        sharingForumLike.setCreatedAt(LocalDateTime.now());
        sharingForumLikesRepository.save(sharingForumLike);
        return forum;
    }

    public SharingForum editForum(SharingForum forum, ForumDTO forumDTO) {
        forum.setJudul(forumDTO.getJudul());
        forum.setContent(forumDTO.getContent());
        forum.setEditedAt(LocalDateTime.now());
        return sharingForumRepository.save(forum);
    }

    public SharingForum commentForum(SharingForum forum, User user, String comment) {
        SharingForumComments forumComment = new SharingForumComments();
        forumComment.setIdForum(forum);
        forumComment.setIdUser(user);
        forumComment.setComment(comment);
        forumComment.setCreatedAt(LocalDateTime.now());
        forumComment.setEditedAt(LocalDateTime.now());
        sharingForumCommentsRepository.save(forumComment);
        return forum;
    }

    public SharingForum editCommentForum(SharingForum forum, User user, String comment, SharingForumComments forumComment) {
        forumComment.setComment(comment);
        forumComment.setEditedAt(LocalDateTime.now());
        sharingForumCommentsRepository.save(forumComment);
        return forum;
    }

    public ArrayList<Object> getComments(SharingForum forum) {
        ArrayList<Object> comments = new ArrayList<Object>();
        for(SharingForumComments comment : getForumComments(forum)) {
            comments.add(Map.of(
                "id", comment.getId(),
                "comment", comment.getComment(),
                "userId", comment.getIdUser().getId(),
                "createdAt", comment.getCreatedAt(),
                "editedAt", comment.getEditedAt()
            ));
        }
        return comments;
    }

    public List<SharingForumComments> getForumComments(SharingForum forum) {
        return sharingForumCommentsRepository.findAll().stream()
                .filter(comment -> comment.getDeletedAt() == null)
                .filter(comment -> comment.getIdForum().equals(forum))
                .sorted(Comparator.comparing(SharingForumComments::getCreatedAt))
                .collect(Collectors.toList());
    }

    public SharingForum deleteForum(SharingForum forum) {
        forum.setDeletedAt(LocalDate.now());
        return sharingForumRepository.save(forum);
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