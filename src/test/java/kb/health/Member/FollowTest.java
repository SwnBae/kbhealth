package kb.health.Member;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kb.health.Repository.FollowRepository;
import kb.health.Repository.MemberRepository;
import kb.health.Service.MemberService;
import kb.health.domain.Follow;
import kb.health.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class FollowTest {

    // 테스트용
    @PersistenceContext EntityManager em;

    @Autowired MemberRepository memberRepository;
    @Autowired FollowRepository followRepository;
    @Autowired MemberService memberService;

    @Test
    @Rollback(false)
    public void 팔로잉테스트() throws Exception {
        //given
        Member member1 = new Member();
        member1.setUserName("주인공");
        Member member2 = new Member();
        member2.setUserName("얘를 팔로우");

        em.persist(member1);
        em.persist(member2);
        //when
        memberService.follow(member1.getId(), member2.getId());

        //then
        assertTrue(member1.getFollowings().stream()
                        .anyMatch(follow -> follow.getFollowing().equals(member2)),
                "멤버1의 팔로잉 목록에 멤버2가 있어야 합니다.");

        // 멤버2의 팔로워 목록에 멤버1이 포함되어 있는지 확인
        assertTrue(member2.getFollowers().stream()
                        .anyMatch(follow -> follow.getFollower().equals(member1)),
                "멤버2의 팔로워 목록에 멤버1이 있어야 합니다.");
    }

}
