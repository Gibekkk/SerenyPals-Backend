package com.serenypals.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serenypals.restfulapi.model.LoginInfo;

public interface LoginInfoRepository extends JpaRepository<LoginInfo, String> {
}
