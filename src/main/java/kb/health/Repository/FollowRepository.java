package kb.health.Repository;

import jakarta.persistence.EntityManager;
import kb.health.domain.Follow;
import kb.health.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FollowRepository {

    private final EntityManager em;

    public void save(Follow follow) {
        em.persist(follow);
    }

    //내가 팔로워인 사람들 -> 내가 팔로우하고 있는 사람들(팔로잉 목록)
    public List<Follow> findByFollower(Member member) {
        return em.createQuery("select f from Follow f where f.follower = :member", Follow.class)
                .setParameter("member", member)
                .getResultList();
    }

    //나를 팔로우하는 사람들(팔로워 목록)
    public List<Follow> findByFollowing(Member member) {
        return em.createQuery("select f from Follow f where f.following = :member", Follow.class)
                .setParameter("member", member)
                .getResultList();
    }

    //아마 안쓸듯
    public List<Follow> findAll() {
        return em.createQuery("select f from Follow f", Follow.class)
                .getResultList();
    }
}
