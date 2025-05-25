package kb.health.repository;

import kb.health.domain.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    // 해당 관계가 있는지 확인하는 메서드
    Optional<Follow> findByFromIdAndToId(Long fromId, Long toId);

    // 팔로잉 목록 조회 (id만 필요하므로, 커스텀)
    @Query("SELECT f.to.id FROM Follow f WHERE f.from.id = :memberId")
    List<Long> findFollowingIds(@Param("memberId") Long memberId);
}