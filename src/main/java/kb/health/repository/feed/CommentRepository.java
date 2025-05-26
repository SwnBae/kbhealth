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

    // 기존 메서드들
    @Query("SELECT c.post.id, COUNT(c) FROM Comment c WHERE c.post.id IN :postIds GROUP BY c.post.id")
    List<Object[]> countCommentsByPostIds(@Param("postIds") List<Long> postIds);

    Page<Comment> findByPostIdOrderByCreatedDateDesc(Long postId, Pageable pageable);

    int countByPost(Post post);

    // fetch join을 사용해서 Comment와 Post를 함께 조회
    @Query("SELECT c FROM Comment c JOIN FETCH c.post WHERE c.id = :commentId")
    Optional<Comment> findByIdWithPost(@Param("commentId") Long commentId);

    // ✅ 새로 추가: 여러 댓글을 한번에 조회 (N+1 쿼리 해결용)
    @Query("SELECT c FROM Comment c JOIN FETCH c.post WHERE c.id IN :commentIds")
    List<Comment> findByIdsWithPost(@Param("commentIds") List<Long> commentIds);

    // ✅ 새로 추가: 댓글과 작성자 정보까지 한번에 조회
    @Query("""
        SELECT c FROM Comment c 
        JOIN FETCH c.post p 
        JOIN FETCH c.writer w 
        WHERE c.id IN :commentIds
        """)
    List<Comment> findByIdsWithPostAndWriter(@Param("commentIds") List<Long> commentIds);
}