package com.moyeo.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moyeo.main.entity.MoyeoPost;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MoyeoPostRepository extends JpaRepository<MoyeoPost, Long> {

    @Query(value = "SELECT p.moyeo_post_id FROM moyeo_post p JOIN moyeo_public mp ON p.moyeo_post_id = mp.moyeo_post_id WHERE mp.user_id = :userId ORDER BY p.create_time DESC LIMIT 1", nativeQuery = true)
    Long findLatestMoyeoPost(@Param("userId") Long userId);

    @Query("SELECT address1, address2, address3, address4 FROM MoyeoPost WHERE moyeoPostId = :moyeoPostId")
    List<String[]> findAddressByMoyeoPostId(Long moyeoPostId);

    @Query("SELECT mp.createTime FROM MoyeoPost mp WHERE mp.moyeoPostId = :moyeoPostId")
    LocalDateTime findCreateTimeByMoyeoPostId(@Param("moyeoPostId") Long moyeoPostId);

    // 유저가 좋아요 누른 모여 포스트 리스트
    @Query(nativeQuery = true, value = "SELECT mp.*\n"
        + "FROM moyeo_post mp\n"
        + "JOIN moyeo_favorite mf ON mp.moyeo_post_id = mf.moyeo_post_id\n"
        + "WHERE mf.user_id = :userId")
    List<MoyeoPost> findAllFavoriteMoyeoPost(Long userId);

    // 해당 타임라인에서의 퍼블릭한 첫번째 모여 포스트
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

    // 해당 타임라인에서의 첫번째 모여 포스트
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

    // 해당 타임라인에서의 마지막 모여 포스트
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

    // 해당 타임라인에서의 퍼블릭한 모여 포스트 가져오기
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

    // 해당 타임라인에서의 모여 포스트 가져오기
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

    // 해당 타임라인에서의 모여 포스트들의 id 리스트
    @Query(nativeQuery = true, value = "SELECT mp.moyeo_post_id\n"
        + "FROM moyeo_post mp\n"
        + "JOIN (\n"
        + "    SELECT DISTINCT moyeo_timeline_id\n"
        + "    FROM time_line_and_moyeo\n"
        + "    WHERE timeline_id = :timelineId\n"
        + ") tlam ON tlam.moyeo_timeline_id = mp.moyeo_timeline_id\n"
        + "JOIN moyeo_public mpb ON mpb.moyeo_post_id = mp.moyeo_post_id AND mpb.user_id = :userId")
    List<Long> findAllMoyeoPostIdByTimelineId(Long timelineId, Long userId);

    // 메인 피드에서 지역으로 검색 시 조회 결과
    @Query(nativeQuery = true, value = "SELECT mp.*\n"
        + "FROM moyeo_post mp\n"
        + "INNER JOIN moyeo_time_line mt ON mp.moyeo_timeline_id = mt.moyeo_timeline_id\n"
        + "INNER JOIN (\n"
        + "    SELECT moyeo_post_id, SUM(is_deleted) > 0 AS isAnyDeleted, MIN(is_public) = 1 AS isAllPublic\n"
        + "    FROM moyeo_public\n"
        + "    GROUP BY moyeo_post_id\n"
        + ") mpb ON mpb.moyeo_post_id = mp.moyeo_post_id\n"
        + "WHERE (mp.address1 LIKE %:location% OR mp.address2 LIKE %:location% OR mp.address3 LIKE %:location% OR mp.address4 LIKE %:location%)\n"
        + "    AND mt.is_complete = true\n"
        + "    AND mpb.isAnyDeleted = false AND mpb.isAllPublic = true")
    List<MoyeoPost> findAllMainFeedMoyeoPostByLocation(String location);

    // 내 페이지에서 지역으로 검색 시 조회 결과
    @Query(nativeQuery = true, value = "SELECT mp.*\n"
        + "FROM moyeo_post mp\n"
        + "INNER JOIN moyeo_time_line mt ON mp.moyeo_timeline_id = mt.moyeo_timeline_id\n"
        + "INNER JOIN (\n"
        + "    SELECT moyeo_post_id, is_deleted\n"
        + "    FROM moyeo_public\n"
        + "    WHERE user_id = :userId AND is_deleted = false\n"
        + ") mpb ON mpb.moyeo_post_id = mp.moyeo_post_id\n"
        + "WHERE (mp.address1 LIKE %:location% OR mp.address2 LIKE %:location% OR mp.address3 LIKE %:location% OR mp.address4 LIKE %:location%)\n"
        + "  AND mt.is_complete = true\n")
    List<MoyeoPost> findAllMyMoyeoPostByLocation(String location, Long userId);

}
