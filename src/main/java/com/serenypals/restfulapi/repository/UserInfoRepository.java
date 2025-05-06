package com.serenypals.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serenypals.restfulapi.model.UserInfo;

public interface UserInfoRepository extends JpaRepository<UserInfo, String> {
}
