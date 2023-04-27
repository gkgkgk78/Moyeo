package com.moyeo.repository;

import com.moyeo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User getByUserId(Long userId);

    User getByClientId(String clientId);

    Boolean existsByRefreshToken(String refreshToken);

    User findByClientId(String clientId);

    @Query(value = "select u from User u where u.nickname like %:search% order by u.nickname")
    List<User> searchUserByNickname(@Param("search") String search);
}
