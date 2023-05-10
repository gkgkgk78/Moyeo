package com.moyeo.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.moyeo.main.dto.PostMembers;
import com.moyeo.main.entity.MoyeoPost;
import com.moyeo.main.entity.MoyeoTimeLine;
import com.moyeo.main.entity.Post;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MoyeoPostRepository extends JpaRepository<MoyeoPost, Long> {

    @Query(value = "SELECT p.moyeo_post_id FROM moyeo_post p JOIN moyeo_public mp ON p.moyeo_post_id = mp.moyeo_post_id WHERE mp.user_id = :userId ORDER BY p.create_time DESC LIMIT 1", nativeQuery = true)
    Long findLatestMoyeoPost(@Param("userId") Long userId);

    @Query("SELECT address1, address2, address3, address4 FROM MoyeoPost WHERE moyeoPostId = :moyeoPostId")
    List<String[]> findAddressByMoyeoPostId(Long moyeoPostId);

    @Query("SELECT mp.createTime FROM MoyeoPost mp WHERE mp.moyeoPostId = :moyeoPostId")
    LocalDateTime findCreateTimeByMoyeoPostId(@Param("moyeoPostId") Long moyeoPostId);

    @Query(nativeQuery = true, value = "select u.user_id, u.client_id, u.nickname, u.profile_image_url from user u\n"
        + "left join moyeo_public p\n"
        + "on u.user_id = p.user_id\n"
        + "where p.moyeo_post_id = :moyeoPostId;")
    List<PostMembers> findMembers(Long moyeoPostId);

    List<MoyeoPost> findAllByMoyeoTimelineIdAndCreateTimeGreaterThanEqualAndCreateTimeLessThanEqual(MoyeoTimeLine moyeoTimelineId, LocalDateTime joinTime, LocalDateTime finishTime);

    Optional<List<MoyeoPost>> findByAddress1ContainsOrAddress2ContainsOrAddress3ContainsOrAddress4Contains(String location1, String location2, String location3, String location4);


    @Query(nativeQuery = true, value = "SELECT a.* FROM moyeo_post a WHERE a.moyeo_timeline_id IN :moyeoTimelineIdList")
    List<MoyeoPost> findAllByMoyeoTimelineIdIn(List<Long> moyeoTimelineIdList);

    @Query(nativeQuery = true, value = "select * from moyeo_post where moyeo_post_id in :postIdList")
    List<MoyeoPost> findAllMoyeoPostIn(List<Long> postIdList);

    @Query(nativeQuery = true, value = "SELECT * FROM moyeo_post p\n"
        + "WHERE p.moyeo_timeline_id IN :moyeoTimelineIdList\n"
        + "AND p.moyeo_post_id IN (\n"
        + "  SELECT moyeo_post_id\n"
        + "  FROM moyeo_public\n"
        + "  GROUP BY moyeo_post_id\n"
        + "  HAVING SUM(is_deleted) = 0 AND MIN(is_public) = 1\n"
        + ")\n"
        + "LIMIT 1")
    MoyeoPost findFirstVisiblePost(List<Long> moyeoTimelineIdList);

    @Query(nativeQuery = true, value = "SELECT * FROM moyeo_post p\n"
        + "WHERE p.moyeo_timeline_id IN :moyeoTimelineIdList\n"
        + "AND p.moyeo_post_id IN (\n"
        + "  SELECT moyeo_post_id\n"
        + "  FROM moyeo_public\n"
        + "  GROUP BY moyeo_post_id\n"
        + "  HAVING SUM(is_deleted) = 0 AND MIN(is_public) = 1\n"
        + ")\n"
        + "ORDER BY p.moyeo_post_id DESC\n"
        + "LIMIT 1")
    MoyeoPost findLastVisiblePost(List<Long> moyeoTimelineIdList);

    @Query(nativeQuery = true, value = "SELECT * FROM moyeo_post p\n"
        + "WHERE p.moyeo_timeline_id IN :moyeoTimelineIdList\n"
        + "AND p.moyeo_post_id IN :moyeoPostIdList\n"
        + "LIMIT 1")
    MoyeoPost findFirstVisiblePostByUserId(List<Long> moyeoTimelineIdList, List<Long> moyeoPostIdList);

    @Query(nativeQuery = true, value = "SELECT * FROM moyeo_post p\n"
        + "WHERE p.moyeo_timeline_id IN :moyeoTimelineIdList\n"
        + "AND p.moyeo_post_id IN :moyeoPostIdList\n"
        + "ORDER BY p.moyeo_post_id DESC\n"
        + "LIMIT 1")
    MoyeoPost findLastVisiblePostByUserId(List<Long> moyeoTimelineIdList, List<Long> moyeoPostIdList);
}
