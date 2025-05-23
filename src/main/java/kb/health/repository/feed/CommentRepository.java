package kb.health.repository.feed;

import kb.health.domain.feed.Comment;
import kb.health.domain.feed.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {
    @Query("SELECT c.post.id, COUNT(c) FROM Comment c WHERE c.post.id IN :postIds GROUP BY c.post.id")
    List<Object[]> countCommentsByPostIds(@Param("postIds") List<Long> postIds);

    Page<Comment> findByPostIdOrderByCreatedDateDesc(Long postId, Pageable pageable);

    int countByPost(Post post);

    // fetch join을 사용해서 Comment와 Post를 함께 조회
    @Query("SELECT c FROM Comment c JOIN FETCH c.post WHERE c.id = :commentId")
    Optional<Comment> findByIdWithPost(@Param("commentId") Long commentId);
}
