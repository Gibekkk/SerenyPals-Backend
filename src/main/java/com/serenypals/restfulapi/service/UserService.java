package com.serenypals.restfulapi.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.serenypals.restfulapi.repository.UserRepository;
import com.serenypals.restfulapi.util.PasswordHasherMatcher;
import com.serenypals.restfulapi.dto.UserDTO;
import com.serenypals.restfulapi.model.User;

@Service
public class UserService {

}