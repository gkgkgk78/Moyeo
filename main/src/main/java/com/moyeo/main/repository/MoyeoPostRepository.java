package com.moyeo.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.moyeo.main.entity.MoyeoPost;

@Repository
public interface MoyeoPostRepository extends JpaRepository<MoyeoPost, Long> {

    @Query("SELECT p.moyeoPostId FROM MoyeoPost p JOIN MoyeoPublic mp ON p.moyeoPostId = mp.moyeoPostId.moyeoPostId WHERE mp.userId = :userId ORDER BY p.createTime DESC")
    Long findLatestMoyeoPost(long userId);

    @Query("SELECT address1, address2, address3, address4 FROM MoyeoPost WHERE moyeoPostId = :moyeoPostId AND moyeoPostId = :moyeoPostId")
    String[] findAddressByMoyeoPostId(long moyeoPostId);

}
