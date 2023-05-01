package com.moyeo.main.repository;

import com.moyeo.main.entity.Photo;
import com.moyeo.main.entity.Post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    void deleteAllByPostId(Post post);
}