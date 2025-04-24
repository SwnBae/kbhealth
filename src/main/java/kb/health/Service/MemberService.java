package kb.health.Service;

import kb.health.Exception.FollowException;
import kb.health.Exception.MemberException;
import kb.health.Repository.FollowRepository;
import kb.health.Repository.MemberRepository;
import kb.health.domain.Follow;
import kb.health.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;

    /**
     * 회원
     */

    //회원 저장
    @Transactional
    public void save(Member member) {
        // 휴대폰 번호 중복 확인
        memberRepository.findMemberByPN(member.getPhoneNumber())
                .ifPresent(m -> {
                    throw MemberException.duplicatePhoneNumber();
                });

        // 이름 중복 확인
        memberRepository.findMemberByName(member.getUserName())
                .ifPresent(m -> {
                    throw MemberException.duplicateUserName();
                });

        memberRepository.save(member);
    }

    //회원 수정 (비밀번호, 닉네임 변경?)


    //휴대폰 번호로 찾기
    public Member findMemberByPhoneNumber(String phoneNumber) {
        return memberRepository.findMemberByPN(phoneNumber)
                .orElseThrow(() -> MemberException.memberNotFoundByPhoneNumber());
    }

    //이름으로 찾기
    public Member findMemberByUserName(String userName) {
        return memberRepository.findMemberByName(userName)
                .orElseThrow(() -> MemberException.memberNotFoundByUserName());
    }

    /**
     * 아래의 멤버 찾는 메서드는 내부 로직에서 사용할 예비 메서드
     */
    //휴대폰 번호로 MemberId 찾기
    public Long findIdByPhoneNumber(String phoneNumber) {
        return memberRepository.findMemberByPN(phoneNumber)
                .map(Member::getId)
                .orElseThrow(() -> MemberException.memberNotFoundByPhoneNumber());
    }

    //고유 값(PK)로 찾기, 내부 로직 사용
    public Member findById(Long id){
        return memberRepository.findMemberById(id);
    }

    /**
     * FOLLOW
     */
    
    //팔로우 기능, 이미 팔로우 한 사람은 취소만 가능 (버튼으로 구현)
    @Transactional
    public void follow(Long myId, Long targetId) {
        if (myId.equals(targetId)) {
            throw FollowException.cannotFollowYourself();
        }

        //영속화
        Member me = memberRepository.findMemberById(myId);
        Member target = memberRepository.findMemberById(targetId);

        // 팔로우 객체 생성
        Follow follow = Follow.createFollow(me, target);

        // 팔로우 엔티티 저장
        followRepository.save(follow);
    }

    //팔로우 취소
    @Transactional
    public void unfollow(Long myId, Long targetId) {
        Follow follow = followRepository.findFollow(myId, targetId)
                .orElseThrow(() -> FollowException.followNotFound());

        follow.disconnect();

        followRepository.delete(follow);
    }

    //팔로잉 목록 조회
    public List<Member> getFollowings(Long memberId) {
        Member member = memberRepository.findMemberById(memberId);
        return member.getFollowings().stream()
                .map(Follow::getTo)
                .toList();
    }

    //팔로워 목록 조회
    public List<Member> getFollowers(Long memberId) {
        Member member = memberRepository.findMemberById(memberId);
        return member.getFollowers().stream()
                .map(Follow::getFrom)
                .toList();
    }
}
