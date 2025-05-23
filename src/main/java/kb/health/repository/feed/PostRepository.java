package kb.health.repository.feed;
import kb.health.domain.feed.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByWriterIdInOrderByCreatedDateDesc(List<Long> writerIds, Pageable pageable);

    Page<Post> findByWriterIdOrderByCreatedDateDesc(Long writerId, Pageable pageable);



}
