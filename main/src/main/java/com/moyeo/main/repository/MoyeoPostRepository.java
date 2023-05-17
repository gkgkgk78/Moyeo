package com.moyeo.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moyeo.main.dto.MemberInfoRes;
import com.moyeo.main.entity.MoyeoPost;
import com.moyeo.main.entity.MoyeoTimeLine;
import com.moyeo.main.entity.Post;

import java.time.LocalDateTime;
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
    List<MemberInfoRes> findMembers(Long moyeoPostId);

    List<MoyeoPost> findAllByMoyeoTimelineIdAndCreateTimeGreaterThanEqualAndCreateTimeLessThanEqual(MoyeoTimeLine moyeoTimelineId, LocalDateTime joinTime, LocalDateTime finishTime);

    Optional<List<MoyeoPost>> findByAddress1ContainsOrAddress2ContainsOrAddress3ContainsOrAddress4Contains(String location1, String location2, String location3, String location4);


    @Query(nativeQuery = true, value = "SELECT mp.*\n"
        + "FROM moyeo_post mp\n"
        + "JOIN moyeo_favorite mf ON mp.moyeo_post_id = mf.moyeo_post_id\n"
        + "WHERE mf.user_id = :userId")
    List<MoyeoPost> findAllFavoriteMoyeoPost(Long userId);

    @Query(nativeQuery = true, value = "SELECT a.* FROM moyeo_post a WHERE a.moyeo_timeline_id IN :moyeoTimelineIdList")
    List<MoyeoPost> findAllByMoyeoTimelineIdIn(List<Long> moyeoTimelineIdList);

    @Query(nativeQuery = true, value = "select * from moyeo_post where moyeo_post_id in :postIdList")
    List<MoyeoPost> findAllMoyeoPostIn(List<Long> postIdList);

    @Query(nativeQuery = true, value = "SELECT * FROM moyeo_post p\n"
        + "WHERE p.moyeo_timeline_id IN :moyeoTimelineIdList\n"
        + "AND p.moyeo_post_id IN :moyeoPostIdList\n"
        + "LIMIT 1")
    MoyeoPost findFirstMoyeoPostByCondition(List<Long> moyeoTimelineIdList, List<Long> moyeoPostIdList);

    @Query(nativeQuery = true, value = "SELECT * FROM moyeo_post p\n"
        + "WHERE p.moyeo_timeline_id IN :moyeoTimelineIdList\n"
        + "AND p.moyeo_post_id IN :moyeoPostIdList\n"
        + "ORDER BY p.moyeo_post_id DESC\n"
        + "LIMIT 1")
    MoyeoPost findLastMoyeoPostByCondition(List<Long> moyeoTimelineIdList, List<Long> moyeoPostIdList);

    @Query(nativeQuery = true, value = "SELECT mp.*\n"
        + "FROM time_line tl\n"
        + "JOIN time_line_and_moyeo tlam ON tl.timeline_id = tlam.timeline_id\n"
        + "JOIN moyeo_time_line mtl ON tlam.moyeo_timeline_id = mtl.moyeo_timeline_id\n"
        + "JOIN moyeo_post mp ON mtl.moyeo_timeline_id = mp.moyeo_timeline_id\n"
        + "JOIN moyeo_public mpb ON mp.moyeo_post_id = mpb.moyeo_post_id\n"
        + "WHERE tl.timeline_id = :timelineId\n"
        + "AND mpb.user_id = :userId AND mpb.is_deleted = false AND mpb.is_public = true\n"
        + "GROUP BY mp.moyeo_post_id\n"
        + "LIMIT 1")
    MoyeoPost findFirstPublicMoyeoPost(Long timelineId, Long userId);

    @Query(nativeQuery = true, value = "SELECT mp.*\n"
        + "FROM time_line tl\n"
        + "JOIN time_line_and_moyeo tlam ON tl.timeline_id = tlam.timeline_id\n"
        + "JOIN moyeo_time_line mtl ON tlam.moyeo_timeline_id = mtl.moyeo_timeline_id\n"
        + "JOIN moyeo_post mp ON mtl.moyeo_timeline_id = mp.moyeo_timeline_id\n"
        + "JOIN moyeo_public mpb ON mp.moyeo_post_id = mpb.moyeo_post_id\n"
        + "WHERE tl.timeline_id = :timelineId\n"
        + "AND mpb.user_id = :userId AND mpb.is_deleted = false AND mpb.is_public = true\n"
        + "AND mp.create_time < :compareCreateTime\n"
        + "GROUP BY mp.moyeo_post_id\n"
        + "LIMIT 1")
    MoyeoPost findFirstPublicMoyeoPostByCreateTimeLessThan(Long timelineId, Long userId, LocalDateTime compareCreateTime);

    @Query(nativeQuery = true, value = "SELECT mp.*\n"
        + "FROM time_line tl\n"
        + "JOIN time_line_and_moyeo tlam ON tl.timeline_id = tlam.timeline_id\n"
        + "JOIN moyeo_time_line mtl ON tlam.moyeo_timeline_id = mtl.moyeo_timeline_id\n"
        + "JOIN moyeo_post mp ON mtl.moyeo_timeline_id = mp.moyeo_timeline_id\n"
        + "JOIN moyeo_public mpb ON mp.moyeo_post_id = mpb.moyeo_post_id\n"
        + "WHERE tl.timeline_id = :timelineId\n"
        + "AND mpb.user_id = :userId AND mpb.is_deleted = false\n"
        + "GROUP BY mp.moyeo_post_id\n"
        + "LIMIT 1")
    MoyeoPost findFirstMoyeoPost(Long timelineId, Long userId);

    @Query(nativeQuery = true, value = "SELECT mp.*\n"
        + "FROM time_line tl\n"
        + "JOIN time_line_and_moyeo tlam ON tl.timeline_id = tlam.timeline_id\n"
        + "JOIN moyeo_time_line mtl ON tlam.moyeo_timeline_id = mtl.moyeo_timeline_id\n"
        + "JOIN moyeo_post mp ON mtl.moyeo_timeline_id = mp.moyeo_timeline_id\n"
        + "JOIN moyeo_public mpb ON mp.moyeo_post_id = mpb.moyeo_post_id\n"
        + "WHERE tl.timeline_id = :timelineId\n"
        + "AND mpb.user_id = :userId AND mpb.is_deleted = false\n"
        + "AND mp.create_time < :compareCreateTime\n"
        + "GROUP BY mp.moyeo_post_id\n"
        + "LIMIT 1")
    MoyeoPost findFirstMoyeoPostByCreateTimeLessThan(Long timelineId, Long userId, LocalDateTime compareCreateTime);

    @Query(nativeQuery = true, value = "SELECT mp.*\n"
        + "FROM time_line tl\n"
        + "JOIN time_line_and_moyeo tlam ON tl.timeline_id = tlam.timeline_id\n"
        + "JOIN moyeo_time_line mtl ON tlam.moyeo_timeline_id = mtl.moyeo_timeline_id\n"
        + "JOIN moyeo_post mp ON mtl.moyeo_timeline_id = mp.moyeo_timeline_id\n"
        + "JOIN moyeo_public mpb ON mp.moyeo_post_id = mpb.moyeo_post_id\n"
        + "WHERE tl.timeline_id = :timelineId\n"
        + "AND mpb.user_id = :userId AND mpb.is_deleted = false\n"
        + "GROUP BY mp.moyeo_post_id\n"
        + "ORDER BY moyeo_post_id DESC\n"
        + "LIMIT 1")
    MoyeoPost findLastMoyeoPost(Long timelineId, Long userId);

    @Query(nativeQuery = true, value = "SELECT mp.*\n"
        + "FROM time_line tl\n"
        + "JOIN time_line_and_moyeo tlam ON tl.timeline_id = tlam.timeline_id\n"
        + "JOIN moyeo_time_line mtl ON tlam.moyeo_timeline_id = mtl.moyeo_timeline_id\n"
        + "JOIN moyeo_post mp ON mtl.moyeo_timeline_id = mp.moyeo_timeline_id\n"
        + "JOIN moyeo_public mpb ON mp.moyeo_post_id = mpb.moyeo_post_id\n"
        + "WHERE tl.timeline_id = :timelineId\n"
        + "AND mpb.user_id = :userId AND mpb.is_deleted = false\n"
        + "AND mp.create_time > :compareCreateTime\n"
        + "GROUP BY mp.moyeo_post_id\n"
        + "ORDER BY moyeo_post_id DESC\n"
        + "LIMIT 1")
    MoyeoPost findLastMoyeoPostByCreateTimeGreaterThan(Long timelineId, Long userId, LocalDateTime compareCreateTime);

    @Query(nativeQuery = true, value = "SELECT mp.*\n"
        + "FROM time_line tl\n"
        + "JOIN time_line_and_moyeo tlam ON tl.timeline_id = tlam.timeline_id\n"
        + "JOIN moyeo_time_line mtl ON tlam.moyeo_timeline_id = mtl.moyeo_timeline_id\n"
        + "JOIN moyeo_post mp ON mtl.moyeo_timeline_id = mp.moyeo_timeline_id\n"
        + "JOIN moyeo_public mpb ON mp.moyeo_post_id = mpb.moyeo_post_id\n"
        + "WHERE tl.timeline_id = :timelineId\n"
        + "AND mpb.user_id = :userId AND mpb.is_deleted = false AND mpb.is_public = true\n"
        + "GROUP BY mp.moyeo_post_id")
    List<MoyeoPost> findAllPublicMoyeoPost(Long timelineId, Long userId);

    @Query(nativeQuery = true, value = "SELECT mp.*\n"
        + "FROM time_line tl\n"
        + "JOIN time_line_and_moyeo tlam ON tl.timeline_id = tlam.timeline_id\n"
        + "JOIN moyeo_time_line mtl ON tlam.moyeo_timeline_id = mtl.moyeo_timeline_id\n"
        + "JOIN moyeo_post mp ON mtl.moyeo_timeline_id = mp.moyeo_timeline_id\n"
        + "JOIN moyeo_public mpb ON mp.moyeo_post_id = mpb.moyeo_post_id\n"
        + "WHERE tl.timeline_id = :timelineId\n"
        + "AND mpb.user_id = :userId AND mpb.is_deleted = false\n"
        + "GROUP BY mp.moyeo_post_id")
    List<MoyeoPost> findAllMoyeoPost(Long timelineId, Long userId);

    @Query(nativeQuery = true, value = "SELECT mp.moyeo_post_id\n"
        + "FROM moyeo_post mp\n"
        + "JOIN (\n"
        + "    SELECT DISTINCT moyeo_timeline_id\n"
        + "    FROM time_line_and_moyeo\n"
        + "    WHERE timeline_id = :timelineId\n"
        + ") tlam ON tlam.moyeo_timeline_id = mp.moyeo_timeline_id\n"
        + "JOIN moyeo_public mpb ON mpb.moyeo_post_id = mp.moyeo_post_id AND mpb.user_id = :userId")
    List<Long> findAllMoyeoPostIdByTimelineId(Long timelineId, Long userId);
}
