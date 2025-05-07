package kb.health.service;

import kb.health.exception.FollowException;
import kb.health.exception.LoginException;
import kb.health.exception.MemberException;
import kb.health.repository.FollowRepository;
import kb.health.repository.MemberRepository;
import kb.health.controller.request.MemberBodyInfoEditRequest;
import kb.health.controller.request.MemberRegistRequest;
import kb.health.domain.Follow;
import kb.health.domain.Member;
import kb.health.controller.request.MemberEditRequest;
import kb.health.controller.response.FollowResponse;
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
    public Long save(MemberRegistRequest request) {
        // 휴대폰 번호 중복 확인
        memberRepository.findMemberByPN(request.getPhoneNumber())
                .ifPresent(m -> {
                    throw MemberException.duplicatePhoneNumber();
                });

        // 이름 중복 확인
        memberRepository.findMemberByName(request.getUserName())
                .ifPresent(m -> {
                    throw MemberException.duplicateUserName();
                });

        // 계정 중복 확인
        memberRepository.findMemberByAccount(request.getAccount())
                .ifPresent(m -> {
                    throw MemberException.duplicateAccount();
                });

        Member member = Member.create(request);

        memberRepository.save(member);
        return member.getId();
    }

    //회원 저장
    @Transactional
    public Long save(Member member) {
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

        // 계정 중복 확인
        memberRepository.findMemberByAccount(member.getAccount())
                .ifPresent(m -> {
                    throw MemberException.duplicateAccount();
                });

        memberRepository.save(member);
        return member.getId();
    }

    /**
     * 회원 수정
     */
    //계정 정보 수정
    @Transactional
    public void updateMember(Long memberId, MemberEditRequest memberEditRequest) {
        Member member = memberRepository.findMemberById(memberId);

        member.updateAccountInfo(memberEditRequest);
    }

    //신체 정보 수정
    @Transactional
    public void updateMemberBodyInfo(Long memberId, MemberBodyInfoEditRequest bodyInfoEditRequest) {
        Member member = memberRepository.findMemberById(memberId);

        member.updateBodyInfo(bodyInfoEditRequest);
    }

    //프로필 사진 수정
    @Transactional
    public void updateProfileImage(Long memberId, String imageUrl) {
        Member member = memberRepository.findMemberById(memberId);
        member.setProfileImageUrl(imageUrl);
    }

    /**
     * 로그인 기능
     */
    public boolean login(String account, String password) {
        Member member = getMemberByAccount(account);

        if (!member.getPassword().equals(password)) {
            throw LoginException.invalidPassword();
        }

        return true;
    }

    /**
     * 회원 찾기
     */
    // 휴대폰 번호로 찾기
    public Member findMemberByPhoneNumber(String phoneNumber) {
        return memberRepository.findMemberByPN(phoneNumber)
                .orElseThrow(() -> MemberException.memberNotFoundByPhoneNumber());
    }

    // 이름으로 찾기
    public Member findMemberByUserName(String userName) {
        return memberRepository.findMemberByName(userName)
                .orElseThrow(() -> MemberException.memberNotFoundByUserName());
    }

    // 계정으로 찾기
    public Member findMemberByAccount(String account) {
        return memberRepository.findMemberByAccount(account)
                .orElseThrow(() -> MemberException.memberNotFoundByAccount());
    }

    //account로 휴대폰번호 찾기
    public String findPNByAccount(String account) {
        return memberRepository.findMemberByAccount(account)
                .map(Member::getPhoneNumber)
                .orElseThrow(() -> MemberException.memberNotFoundByPhoneNumber());
    }

    /**
     * 아래의 멤버 찾는 메서드는 내부 로직에서 사용할 예비 메서드
     */
    public Member getMemberByAccount(String account) {
        return memberRepository.findMemberByAccount(account)
                .orElseThrow(() -> MemberException.memberNotFoundByAccount());
    }

    //고유 값(PK)로 찾기, 내부 로직 사용
    public Member findById(Long id) {
        return memberRepository.findMemberById(id);
    }

    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    /**
     * FOLLOW
     */
    //팔로우 기능
    @Transactional
    public void follow(Long myId, Long targetId) {
        if (myId.equals(targetId)) {
            throw FollowException.cannotFollowYourself();
        }

        // 이미 팔로우 중인지 확인
        if (followRepository.findFollow(myId, targetId).isPresent()) {
//            throw FollowException.alreadyFollowing(); // 이미 팔로우 중인 경우 예외 발생
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

    public List<FollowResponse> getFollowings(Long memberId) {
        Member member = memberRepository.findMemberById(memberId);
        return member.getFollowings().stream()
                .map(follow -> FollowResponse.create(follow.getTo()))
                .toList();
    }

    public List<FollowResponse> getFollowers(Long memberId) {
        Member member = memberRepository.findMemberById(memberId);
        return member.getFollowers().stream()
                .map(follow -> FollowResponse.create(follow.getFrom()))
                .toList();
    }
}