package com.moyeo.main.repository;

import com.moyeo.main.dto.GetFavoritePostListRes;
import com.moyeo.main.entity.Favorite;
import com.moyeo.main.id.FavoriteID;
import com.moyeo.main.entity.Post;
import com.moyeo.main.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteID> {
    // 해당 포스트에 해당 유저가 좋아요 눌렀는지 여부 검색
    Favorite findFirstByPostIdAndUserId(Post post, User user);

    // 포스트에 눌린 좋아요 수 검색
    Long countByPostId(Post post);

    // 해당 유저가 좋아요 누른 포스트 검색
    Optional<List<Favorite>> findAllByUserId(User user);

    //포스트 삭제되면 포스트에 연결된 좋아요도 삭제
    void deleteAllByPostId(Post post);

    // TODO
    @Query(nativeQuery = true, value = "select post_id, create_time, modify_time, address1, address2, address3, address4, favorite_count, text, voice_length, voice_url, nation_id, timeline_id, user_id, false as is_moyeo\n"
        + "from post where user_id = :userId\n"
        + "union\n"
        + "select a.moyeo_post_id, a.create_time, a.modify_time, a.address1, a.address2, a.address3, a.address4, a.favorite_count, a.text, a.voice_length, a.voice_url, a.nation_id, NULL, b.user_id, true as is_moyeo\n"
        + "from moyeo_post a\n"
        + "left join moyeo_public b\n"
        + "on a.moyeo_post_id = b.moyeo_post_id\n"
        + "where b.user_id = :userId\n"
        + "ORDER BY create_time")
    List<GetFavoritePostListRes> findPostsAndMoyeoPostsByUserId(Long userId);
}
