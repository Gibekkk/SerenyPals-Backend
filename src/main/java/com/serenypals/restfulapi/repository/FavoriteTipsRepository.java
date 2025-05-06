package com.serenypals.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serenypals.restfulapi.model.FavoriteTips;

public interface FavoriteTipsRepository extends JpaRepository<FavoriteTips, String> {
}
