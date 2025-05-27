package kb.health.Member;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kb.health.exception.FollowException;
import kb.health.repository.FollowRepository;
import kb.health.repository.MemberRepository;
import kb.health.service.MemberService;
import kb.health.domain.BodyInfo;
import kb.health.domain.DailyNutritionStandard;
import kb.health.domain.Gender;
import kb.health.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
    public void 팔로잉() throws Exception {
        //given
        Member member1 = createMember();
        member1.setUserName("주인공");
        Member member2 = createMember();
        member2.setUserName("얘를 팔로우");
        member2.setAccount("account2");

        em.persist(member1);
        em.persist(member2);

        //when
        memberService.follow(member1.getId(), member2.getId());

        //then
        assertTrue(member1.getFollowings().stream()
                        .anyMatch(follow -> follow.getTo().equals(member2)),
                "멤버1의 팔로잉 목록에 멤버2가 있어야 합니다.");

        // 멤버2의 팔로워 목록에 멤버1이 포함되어 있는지 확인
        assertTrue(member2.getFollowers().stream()
                        .anyMatch(follow -> follow.getFrom().equals(member1)),
                "멤버2의 팔로워 목록에 멤버1이 있어야 합니다.");
    }

    @Test
    public void 팔로잉_취소() throws Exception {
        //given
        Member member1 = createMember();
        member1.setUserName("주인공");
        Member member2 = createMember();
        member2.setUserName("얘를 팔로우");
        member2.setAccount("account2");

        memberService.save(member1);
        memberService.save(member2);

        memberService.follow(member1.getId(), member2.getId());

        //when
        memberService.unfollow(member1.getId(), member2.getId());

        //then
        assertTrue(member1.getFollowings().isEmpty(), "팔로잉 목록이 비어야 합니다.");
        assertTrue(member2.getFollowers().isEmpty(), "팔로워 목록이 비어야 합니다.");
    }

    @Test
    public void 팔로잉_팔로워_조회() throws Exception {
        // given
        Member me = createMember();
        me.setUserName("me");
        memberService.save(me);

        Member target1 = createMember();
        target1.setUserName("target1");
        target1.setAccount("account1");
        memberService.save(target1);

        Member target2 = createMember();
        target2.setUserName("target2");
        target2.setAccount("account2");
        memberService.save(target2);

        Long myId = me.getId();
        Long target1Id = target1.getId();
        Long target2Id = target2.getId();

        // when
        memberService.follow(myId, target1Id);
        memberService.follow(myId, target2Id);

        // then
        assertEquals(2, memberService.getFollowings(myId).size());
        assertEquals("target1", memberService.getFollowings(myId).get(0).getUserName());
        assertEquals("target2", memberService.getFollowings(myId).get(1).getUserName());

        assertEquals(1, memberService.getFollowers(target1Id).size());
        assertEquals("me", memberService.getFollowers(target1Id).get(0).getUserName());
    }

    @Test
    public void 자신을_팔로우하는_경우() throws Exception {
        //given
        Member member = createMember();
        member.setUserName("주인공");

        memberService.save(member);

        //when, then
        FollowException followException = assertThrows(
                FollowException.class,
                () -> memberService.follow(member.getId(), member.getId()));

        assertEquals(2001, followException.getCode());
    }

    private Member createMember() {
        String account = "account";
        String userName = "member1";
        String password = "password";
        String phoneNumber = "010-0000-0000";

        Member member = Member.create(account, userName, password, phoneNumber);

        // BodyInfo와 DailyNutritionStandard 세팅
        BodyInfo bodyInfo = new BodyInfo(175.0, 70.0, Gender.MALE, 30);
        member.setBodyInfo(bodyInfo);

        DailyNutritionStandard standard = DailyNutritionStandard.calculate(bodyInfo);
        member.setDailyNutritionStandard(standard);

        return member;
    }

}
