package kb.health.repository.feed;

import kb.health.domain.feed.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    @Query("SELECT pl.post.id, COUNT(pl) FROM PostLike pl WHERE pl.post.id IN :postIds GROUP BY pl.post.id")
    List<Object[]> countLikesByPostIds(@Param("postIds") List<Long> postIds);

    @Query("SELECT pl.post.id FROM PostLike pl WHERE pl.member.id = :memberId")
    List<Long> findPostIdsLikedByMember(@Param("memberId") Long memberId);

}
