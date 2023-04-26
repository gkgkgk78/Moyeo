package com.danim.repository;

import com.danim.entity.Photo;
import com.danim.entity.Post;
import com.danim.entity.TimeLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    void deleteAllByPostId(Post post);
}