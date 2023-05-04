package com.moyeo.main.repository;

import com.moyeo.main.entity.Post;
import com.moyeo.main.entity.TimeLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // 지역명으로 post 조회(검색 기능)
    Optional<List<Post>> findByAddress1ContainsOrAddress2ContainsOrAddress3ContainsOrAddress4Contains(String location1, String location2, String location3, String location4);

//    List<Post> findByLocation(String location);

    //Optional<List<TimeLine>> findAllByUserIdOrderByCreateTimeDesc(User u);

    List<Post> findByTimelineIdOrderByCreateTimeAsc(TimeLine timeline) ;

    Post findTopByTimelineIdOrderByCreateTimeAsc(TimeLine timeline) ;

    List<Post> findAllByTimelineIdOrderByCreateTimeAsc(TimeLine timeLine) ;

    Post findTopByTimelineIdOrderByCreateTimeDesc(TimeLine timeline) ;

    List<Post>findAllByTimelineId(TimeLine timeLine);

    @Query(value = "SELECT post_id FROM post WHERE user_id = :userId ORDER BY create_time LIMIT 1", nativeQuery = true)
    Long findLatestPost(@Param("userId") Long userId);

    @Query("SELECT address1, address2, address3, address4 FROM Post WHERE postId = :postId AND userId.userId = :userId")
    List<String[]> findAddressById(Long postId, Long userId);

    @Query("SELECT p.createTime FROM Post p WHERE p.postId = :postId")
    LocalDateTime findCreateTimeByPostId(Long postId);


}
