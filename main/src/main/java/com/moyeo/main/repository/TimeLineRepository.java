package com.moyeo.main.repository;

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
    Optional<TimeLine> findFirstByUserId(User user);
    Optional<TimeLine> findFirstByUserIdAndIsComplete(User user, Boolean isComplete);

    @Override
    TimeLine getById(Long aLong);

    Optional<TimeLine> findFirstByUserIdOrderByTimelineIdDesc(User user);
    Optional<List<TimeLine>> findAllByUserIdOrderByCreateTimeDesc(User u);

    Optional<List<TimeLine>> findAllByUserIdAndIsTimelinePublic(User u, Boolean flag);

    Optional<List<TimeLine>> findAllByUserId(User u);
    Integer countAllByUserId(User u);

    Page<TimeLine> findAll(Pageable pageable);

    //공개되지 않은 것들 중에, 완료가 된것만 찾아내야함
    Page<TimeLine> findAllByIsCompleteAndIsTimelinePublic(Boolean isComplete, Boolean public1, Pageable pageable);
    // 2. No-Offset 방식
    Optional<List<TimeLine>> findTop15ByIsCompleteAndIsTimelinePublicAndTimelineIdLessThanOrderByTimelineIdDesc(Boolean isComplete, Boolean isPublic, Long timeLineId);
    Optional<List<TimeLine>> findTop16ByIsCompleteAndIsTimelinePublicAndTimelineIdLessThanOrderByTimelineIdDesc(Boolean isComplete, Boolean isPublic, Long timeLineId);
    Optional<List<TimeLine>> findTop16ByIsCompleteAndIsTimelinePublicOrderByTimelineIdDesc(Boolean isComplete, Boolean isPublic);

    Page<TimeLine> findAllByUserIdOrderByCreateTimeDesc(User u, Pageable pageable);

    Page<TimeLine> findAllByUserIdAndIsTimelinePublic(User u, Boolean flag, Pageable pageable);


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

    @Query(value = "SELECT tl.*\n"
        + "FROM time_line tl\n"
        + "JOIN (\n"
        + "\tSELECT DISTINCT timeline_id, moyeo_timeline_id\n"
        + "    FROM time_line_and_moyeo\n"
        + ") tlam ON tl.timeline_id = tlam.timeline_id\n"
        + "JOIN moyeo_post mp ON tlam.moyeo_timeline_id = mp.moyeo_timeline_id\n"
        + "WHERE mp.moyeo_post_id = :moyeoPostId", nativeQuery = true)
    List<TimeLine> findAllByMoyeoPostId(Long moyeoPostId);

}
