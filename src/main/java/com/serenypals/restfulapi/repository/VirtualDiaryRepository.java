package com.serenypals.restfulapi.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serenypals.restfulapi.model.VirtualDiary;
import com.serenypals.restfulapi.model.User;

public interface VirtualDiaryRepository extends JpaRepository<VirtualDiary, String> {
    List<VirtualDiary> findAllByIdUser(User user);
}
