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
import java.util.Optional;

import static kb.health.Exception.FollowException.followNotFound;

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
        try {
            memberRepository.save(member);
        } catch (Exception e) {
            throw MemberException.duplicatePhoneNumber();
        }
    }

    //회원 수정 (비밀번호, 닉네임 변경?)


    //휴대폰 번호로 찾기
    public Optional<Member> findMemberByPhoneNumber(String phoneNumber) {
        return memberRepository.findByMemberPN(phoneNumber);
    }

    //휴대폰 번호로 MemberId 찾기
    public Optional<Long> findIdByPhoneNumber(String phoneNumber) {
        return memberRepository.findByMemberPN(phoneNumber).map(Member::getId);
    }

    //고유 값(PK)로 찾기, 내부 로직 사용
    public Member findById(Long id){
        return memberRepository.findById(id);
    }

    /**
     * FOLLOW
     */
    
    //팔로우 기능, 이미 팔로우 한 사람은 취소만 가능 (버튼으로 구현)
    @Transactional
    public void follow(Long myId, Long targetId) {
        if (myId.equals(targetId)) {
            throw FollowException.cannotFollowYourself(myId);
        }

        //영속화
        Member me = memberRepository.findById(myId);
        Member target = memberRepository.findById(targetId);

        // 팔로우 객체 생성
        Follow follow = Follow.createFollow(me, target);

        // 팔로우 엔티티 저장
        followRepository.save(follow);
    }

    //팔로우 취소
    @Transactional
    public void unfollow(Long myId, Long targetId) {
        Follow follow = followRepository.findFollow(myId, targetId)
                .orElseThrow(() -> FollowException.followNotFound(myId,targetId));

        follow.disconnect();

        followRepository.delete(follow);
    }

    //팔로잉 목록 조회
    public List<Member> getFollowings(Long memberId) {
        Member member = memberRepository.findById(memberId);
        return member.getFollowings().stream()
                .map(Follow::getTo)
                .toList();
    }

    //팔로워 목록 조회
    public List<Member> getFollowers(Long memberId) {
        Member member = memberRepository.findById(memberId);
        return member.getFollowers().stream()
                .map(Follow::getFrom)
                .toList();
    }
}
