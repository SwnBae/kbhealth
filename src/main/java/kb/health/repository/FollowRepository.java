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

    // 기존 메서드들
    Optional<Follow> findByFromIdAndToId(Long fromId, Long toId);

    // ✅ 메서드 이름만으로 COUNT 쿼리 생성!
    int countByFromId(Long fromId);        // SELECT COUNT(*) FROM follow WHERE from_id = ?
    int countByToId(Long toId);            // SELECT COUNT(*) FROM follow WHERE to_id = ?


    // ✅ 메서드 이름만으로 존재 여부 확인!
    boolean existsByFromIdAndToId(Long fromId, Long toId);  // SELECT EXISTS(SELECT 1 FROM follow WHERE from_id = ? AND to_id = ?)

    // 복잡한 조회만 @Query 사용 (JOIN FETCH는 메서드 이름으로 불가능)
    @Query("SELECT f.to.id FROM Follow f WHERE f.from.id = :memberId")
    List<Long> findFollowingIds(@Param("memberId") Long memberId);

    @Query("SELECT f FROM Follow f JOIN FETCH f.to WHERE f.from.id = :memberId")
    List<Follow> findFollowingsWithMembers(@Param("memberId") Long memberId);

    @Query("SELECT f FROM Follow f JOIN FETCH f.from WHERE f.to.id = :memberId")
    List<Follow> findFollowersWithMembers(@Param("memberId") Long memberId);
}