package kb.health.Member;

import kb.health.Exception.FollowException;
import kb.health.Exception.MemberException;
import kb.health.Repository.MemberRepository;
import kb.health.Service.MemberService;
import kb.health.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
public class MemberTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    @Test
    public void 회원가입() throws Exception {
        //given
        Member member = createMember();

        //when
        memberService.save(member);
        Member findMember = memberRepository.findMemberById(member.getId());

        //then
        assertEquals(member.getId(), findMember.getId());
    }

    @Test
    public void 휴대폰_번호로_멤버조회() throws Exception {
        //given
        Member member = createMember();
        memberService.save(member);

        //when
        Member findMember = memberService.findMemberByPhoneNumber("123");

        //then
        assertEquals(member, findMember);
    }

    @Test
    public void 닉네임으로_멤버조회() throws Exception {
        //given
        Member member = createMember();
        memberService.save(member);

        //when
        Member findMember = memberService.findMemberByUserName("member1");

        //then
        assertEquals(member, findMember);
    }

    @Test
    public void 중복된_휴대폰번호로_가입시도() throws Exception {
        //given
        Member member1 = createMember();
        memberService.save(member1);

        Member member2 = createMember();
        member2.setPhoneNumber("123");

        //when, then
        assertThrows(
                MemberException.class,
                () -> memberService.save(member2));
    }

    @Test
    public void 중복된_닉네임으로_가입시도() throws Exception {
        //given
        Member member1 = createMember();
        memberService.save(member1);

        //휴대폰 번호만 다른 경우
        Member member2 = createMember();
        member2.setPhoneNumber("456");

        //when, then
        assertThrows(
                MemberException.class,
                () -> memberService.save(member2));
    }

    private Member createMember() {
        Member member = new Member();
        member.setUserName("member1");
        member.setPhoneNumber("123");

        return member;
    }
}
