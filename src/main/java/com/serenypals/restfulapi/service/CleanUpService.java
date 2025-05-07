package com.serenypals.restfulapi.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.serenypals.restfulapi.repository.*;

import org.springframework.transaction.annotation.Transactional;

import com.serenypals.restfulapi.model.*;

@Service
public class CleanUpService {
    
    @Autowired
    private UserRepository userRepository;
    
    // @Autowired
    // private LoginService loginService;

    // @Autowired
    // private UserService userService;

    private int deleteDays = 30;
    private LocalDate today = LocalDate.now();

    private int compareDate(LocalDate deletedAt) {
        int daysElapsed = (int) ChronoUnit.DAYS.between(deletedAt, today);
        return daysElapsed;
    }

    private boolean inDeletion(LocalDate deletedAt) {
        return compareDate(deletedAt) >= deleteDays;
    }

    // @Transactional
    // public void cleanUser() {
    //     for(User user : userRepository.findAll()){
    //         if(user.getIsDeleted() && inDeletion(user.getDeletedAt())) {
    //             userService.deleteProfilePictureById(user.getId());
    //             loginService.deleteSessionByUser(user.getId());
    //             userRepository.delete(user);
    //         }
    //     }
    // }

    // @Transactional
    // public void cleanUser(User user) {
    //     userService.deleteProfilePictureById(user.getId());
    //     loginService.deleteSessionByUser(user.getId());
    //     userRepository.delete(user);
    // }

    @Transactional
    public void fullClean() {
        // cleanUser();
    }
}