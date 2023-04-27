package com.moyeo.repository;

import com.moyeo.entity.Photo;
import com.moyeo.entity.Post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    void deleteAllByPostId(Post post);
}