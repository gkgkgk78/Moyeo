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

}
