package kb.health.Member;

import kb.health.exception.LoginException;
import kb.health.exception.MemberException;
import kb.health.repository.MemberRepository;
import kb.health.service.MemberService;
import kb.health.domain.BodyInfo;
import kb.health.domain.DailyNutritionStandard;
import kb.health.domain.Gender;
import kb.health.domain.Member;
import kb.health.controller.request.MemberEditRequest;
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
//    @Rollback(false)
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
        // given
        Member member = createMember();
        memberService.save(member);

        // when
        Member findMember = memberService.findMemberByPhoneNumber("010-0000-0000");

        // then
        assertEquals(member.getId(), findMember.getId());
        assertEquals(member.getUserName(), findMember.getUserName());
        assertEquals(member.getTotalScore(), findMember.getTotalScore());
        assertEquals(member.getBaseScore(), findMember.getBaseScore());
        assertEquals(member.getProfileImageUrl(), findMember.getProfileImageUrl());
    }

    @Test
    public void 계정으로_멤버조회() throws Exception {
        // given
        Member member = createMember();
        memberService.save(member);

        // when
        Member findMember = memberService.findMemberByAccount("account");

        // then
        assertEquals(member.getId(), findMember.getId());
        assertEquals(member.getUserName(), findMember.getUserName());
        assertEquals(member.getTotalScore(), findMember.getTotalScore());
        assertEquals(member.getBaseScore(), findMember.getBaseScore());
        assertEquals(member.getProfileImageUrl(), findMember.getProfileImageUrl());
    }

    @Test
    public void 닉네임으로_멤버조회() throws Exception {
        // given
        Member member = createMember();
        memberService.save(member);

        // when
        Member findMember = memberService.findMemberByUserName("member1");

        // then
        assertEquals(member.getId(), findMember.getId());
        assertEquals(member.getUserName(), findMember.getUserName());
        assertEquals(member.getTotalScore(), findMember.getTotalScore());
        assertEquals(member.getBaseScore(), findMember.getBaseScore());
        assertEquals(member.getProfileImageUrl(), findMember.getProfileImageUrl());
    }

    @Test
    public void 중복된_휴대폰번호로_가입시도() throws Exception {
        //given
        Member member1 = createMember();
        memberService.save(member1);

        Member member2 = createMember();
        member2.setAccount("tmp");
        member2.setUserName("tmpName");

        //when
        MemberException exception = assertThrows(
                MemberException.class,
                () -> memberService.save(member2)
        );

        //then
        assertEquals(1001, exception.getCode()); // ExceptionCode.DUPLICATE_PHONE_NUMBER.getCode()
    }


    @Test
    public void 중복된_닉네임으로_가입시도() throws Exception {
        //given
        Member member1 = createMember();
        memberService.save(member1);

        //닉네임만 같은 경우
        Member member2 = createMember();
        member2.setAccount("tmp");
        member2.setPhoneNumber("011-0000-0000");

        //when, then
        MemberException exception = assertThrows(
                MemberException.class,
                () -> memberService.save(member2)
        );

        assertEquals(1002, exception.getCode());
    }

    @Test
    public void 중복된_계정으로_가입시도() throws Exception {
        //given
        Member member1 = createMember();
        memberService.save(member1);

        //계정만 같은 경우
        Member member2 = createMember();
        member2.setUserName("tmpName");
        member2.setPhoneNumber("011-0000-0000");

        //when, then
        MemberException exception = assertThrows(

                MemberException.class,
                () -> memberService.save(member2)
        );

        assertEquals(1003, exception.getCode());
    }

    @Test
    public void 존재하지_않는_휴대폰번호로_조회시_예외() {
        // when
        MemberException exception = assertThrows(
                MemberException.class,
                () -> memberService.findMemberByPhoneNumber("010-9999-9999")
        );

        // then
        assertEquals(1004, exception.getCode());
    }

    @Test
    public void 존재하지_않는_닉네임으로_조회시_예외() {
        // when
        MemberException exception = assertThrows(
                MemberException.class,
                () -> memberService.findMemberByUserName("nonexistentUser")
        );

        // then
        assertEquals(1005, exception.getCode());
    }

    @Test
    public void 존재하지_않는_계정으로_조회시_예외() {
        // when
        MemberException exception = assertThrows(
                MemberException.class,
                () -> memberService.findMemberByAccount("nonexistentAccount")
        );

        // then
        assertEquals(1006, exception.getCode());
    }


    @Test
    public void 회원_수정() throws Exception {
        // given
        Member member = createMember();
        Long memberId = memberService.save(member);

        MemberEditRequest form = new MemberEditRequest();
        form.setPassword("new_password");
        form.setUserName("updatedName");
        form.setProfileImageUrl("http://example.com/profile.jpg");

        // when
        memberService.updateMember(memberId, form);
        Member updatedMember = memberRepository.findMemberById(memberId);

        // then
        assertEquals("new_password", updatedMember.getPassword());
        assertEquals("updatedName", updatedMember.getUserName());
        assertEquals("http://example.com/profile.jpg", updatedMember.getProfileImageUrl());
    }

    @Test
    public void 로그인_성공() throws Exception {
        // given
        Member member = createMember();
        memberService.save(member);

        // when
        boolean result = memberService.login("account", "password");

        // then
        assertTrue(result);
    }

    @Test
    public void 로그인_비밀번호_틀림() {
        // given
        Member member = createMember();
        memberService.save(member);

        // when
        LoginException exception = assertThrows(
                LoginException.class,
                () -> memberService.login("account", "wrongpassword")
        );

        // then
        assertEquals(3003, exception.getCode());
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
