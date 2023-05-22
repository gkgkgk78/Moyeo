package com.moyeo.main.repository;

import com.moyeo.main.entity.Post;
import com.moyeo.main.entity.TimeLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // 해당 타임라인에서 제일 마지막 포스트
    Post findTopByTimelineIdOrderByPostIdDesc(TimeLine timeline);
    // 해당 타임라인에서 제일 첫번째 포스트
    Post findTopByTimelineId(TimeLine timeline);

    List<Post>findAllByTimelineId(TimeLine timeLine);

    @Query(value = "SELECT post_id FROM post WHERE user_id = :userId ORDER BY create_time DESC LIMIT 1", nativeQuery = true)
    Long findLatestPost(@Param("userId") Long userId);

    @Query("SELECT address1, address2, address3, address4 FROM Post WHERE postId = :postId AND userId.userId = :userId")
    List<String[]> findAddressById(Long postId, Long userId);

    @Query("SELECT p.createTime FROM Post p WHERE p.postId = :postId")
    LocalDateTime findCreateTimeByPostId(Long postId);

    // 좋아요 누른 포스트 가져오기
    @Query(nativeQuery = true, value = "SELECT p.*\n"
        + "FROM post p\n"
        + "JOIN favorite f ON p.post_id = f.post_id\n"
        + "WHERE f.user_id = :userId")
    List<Post> findAllFavoritePost(Long userId);

    // 메인 피드에서 지역으로 검색 시 조회 결과
    @Query(nativeQuery = true, value = "SELECT p.*\n"
        + "FROM post p\n"
        + "INNER JOIN time_line t ON p.timeline_id = t.timeline_id\n"
        + "WHERE (p.address1 LIKE %:location% OR p.address2 LIKE %:location% OR p.address3 LIKE %:location% OR p.address4 LIKE %:location%)\n"
        + "  AND t.is_timeline_public = true\n"
        + "  AND t.is_complete = true")
    List<Post> findAllMainFeedPostByLocation(String location);

    // 내 페이지에서 지역으로 검색 시 조회 결과
    @Query(nativeQuery = true, value = "SELECT p.*\n"
        + "FROM post p\n"
        + "INNER JOIN time_line t ON p.timeline_id = t.timeline_id\n"
        + "WHERE (p.address1 LIKE %:location% OR p.address2 LIKE %:location% OR p.address3 LIKE %:location% OR p.address4 LIKE %:location%)\n"
        + "  AND p.user_id = :userId\n"
        + "  AND t.is_complete = true")
    List<Post> findAllMyPostByLocation(String location, Long userId);

}
