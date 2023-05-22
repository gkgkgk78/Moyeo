package com.moyeo.main.repository;

import com.moyeo.main.dto.GetTimelineListRes;
import com.moyeo.main.entity.TimeLine;
import com.moyeo.main.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


//mapper와 동일한 기능을 제공한다고 생각을 하자
@Repository
public interface TimeLineRepository extends JpaRepository<TimeLine, Long> {
    Optional<TimeLine> findFirstByUserIdAndIsComplete(User user, Boolean isComplete);

    @Override
    TimeLine getById(Long aLong);

    Integer countAllByUserId(User u);

    Page<TimeLine> findAll(Pageable pageable);

    Page<TimeLine> findAllByIsCompleteAndIsTimelinePublic(Boolean isComplete, Boolean public1, Pageable pageable);
    Page<TimeLine> findAllByUserIdOrderByCreateTimeDesc(User u, Pageable pageable);
    Page<TimeLine> findAllByUserIdAndIsTimelinePublicAndIsComplete(User u, Boolean isTimelinePublic, Boolean isComplete, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update TimeLine set is_complete= :now")
    void changeTimeline(Boolean now);

    TimeLine findAllByUserIdAndIsComplete(User u, Boolean flag);

    // @Query(value = "select u from User u where u.nickname like %:search% order by u.nickname")
    @Query(value = "select timeline_id from time_line order by timeline_id desc limit 1", nativeQuery = true)
    Long findLastTimelineId();

    @Query(value = "SELECT CASE WHEN is_complete = true THEN 1 ELSE 0 END FROM time_line WHERE user_id = :userId ORDER BY create_time DESC LIMIT 1", nativeQuery = true)
    int findLatestTimelineStatus(Long userId);

    // 해당 모여 포스트를 가지고 있는 타임라인 가져오기
    @Query(value = "SELECT tl.*\n"
        + "FROM time_line tl\n"
        + "JOIN (\n"
        + "    SELECT DISTINCT timeline_id, moyeo_timeline_id\n"
        + "    FROM time_line_and_moyeo\n"
        + ") tlam ON tl.timeline_id = tlam.timeline_id\n"
        + "JOIN moyeo_post mp ON tlam.moyeo_timeline_id = mp.moyeo_timeline_id\n"
        + "WHERE mp.moyeo_post_id = :moyeoPostId AND tl.is_complete = true AND tl.is_timeline_public = true", nativeQuery = true)
    List<TimeLine> findAllByMoyeoPostId(Long moyeoPostId);

    // 해당 모여 포스트를 가지고 있는 내 타임라인
    @Query(value = "SELECT tl.*\n"
        + "FROM time_line tl\n"
        + "JOIN (\n"
        + "    SELECT DISTINCT timeline_id, moyeo_timeline_id\n"
        + "    FROM time_line_and_moyeo\n"
        + ") tlam ON tl.timeline_id = tlam.timeline_id\n"
        + "JOIN moyeo_post mp ON tlam.moyeo_timeline_id = mp.moyeo_timeline_id\n"
        + "WHERE mp.moyeo_post_id = :moyeoPostId AND tl.is_complete = true AND tl.user_id = :userId", nativeQuery = true)
    List<TimeLine> findAllMineByMoyeoPostId(Long moyeoPostId, Long userId);


    @Query(nativeQuery = true, value = "SELECT tl.timeline_id AS timelineId, tl.title, tl.user_id AS userId, u.nickname, p.photo_url AS photoUrl, p.create_time AS startTime, p.address2 AS startPlace, lp.create_time AS lastTime, p.address2 AS lastPlace\n"
        + "FROM time_line tl\n"
        + "LEFT JOIN user u ON tl.user_id = u.user_id\n"
        + "LEFT JOIN (\n"
        + "    SELECT p2.*, ph.photo_url, ROW_NUMBER() OVER (PARTITION BY timeline_id) AS rn\n"
        + "    FROM post p2\n"
        + "    LEFT JOIN (\n"
        + "        SELECT *, ROW_NUMBER() OVER (PARTITION BY post_id) AS rn\n"
        + "        FROM photo ph\n"
        + "    ) ph ON ph.post_id = p2.post_id AND ph.rn = 1\n"
        + ") p ON tl.timeline_id = p.timeline_id AND p.rn = 1\n"
        + "LEFT JOIN (\n"
        + "    SELECT *, ROW_NUMBER() OVER (PARTITION BY timeline_id ORDER BY post_id DESC) AS rn\n"
        + "    FROM post\n"
        + ") lp ON tl.timeline_id = lp.timeline_id AND lp.rn = 1\n"
        + "WHERE tl.is_complete = true AND tl.is_timeline_public = true")
    List<GetTimelineListRes> getPublicTimelineList();

}
