package kb.health.Repository;

import jakarta.persistence.EntityManager;
import kb.health.domain.Follow;
import kb.health.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FollowRepository {

    private final EntityManager em;

    //팔로우 저장
    public void save(Follow follow) {
        em.persist(follow);
    }

    //팔로우 삭제
    public void delete(Follow follow) {
        em.remove(follow);
    }
    
    //해당 관계가 있는지 확인하는 메서드
    public Optional<Follow> findFollow(Long fromId, Long toId) {
        return em.createQuery("select f from Follow f where f.from.id = :fromId and f.to.id = :toId", Follow.class)
                .setParameter("fromId", fromId)
                .setParameter("toId", toId)
                .getResultStream()
                .findFirst();  // 없을 경우 Optional.empty() 반환
    }

    /**
     * 아래 메서드들은 안쓸거 같음, 예비 메서드
     */

    public List<Follow> findAll() {
        return em.createQuery("select f from Follow f", Follow.class)
                .getResultList();
    }

    //내가 팔로워인 사람들 -> 내가 팔로우하고 있는 사람들(팔로잉 목록)
    public List<Follow> findFollowingByMemberId(Long memberId) {
        return em.createQuery("select f from Follow f where f.from.id = :memberId", Follow.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    // 나를 팔로우하는 사람들(팔로워 목록)
    public List<Follow> findFollowersByMemberId(Long memberId) {
        return em.createQuery("select f from Follow f where f.to.id = :memberId", Follow.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }
}
