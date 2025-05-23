package kb.health.service;

import kb.health.controller.response.RankingResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // 아이디 중복 검사
    public boolean isAccountDuplicate(String account) {
        return memberRepository.findMemberByAccount(account).isPresent();
    }

    // 닉네임 중복 검사
    public boolean isUsernameDuplicate(String userName) {
        return memberRepository.findMemberByName(userName).isPresent();
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

    // 닉네임으로 찾기
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

    // 유저 검색 (Account, userName)
    public List<Member> searchByUserNameOrAccountLike(String keyword) {
        return memberRepository.findByUserNameOrAccountLike(keyword);
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

    public boolean isFollowing(Long myId, Long targetId) {
        return followRepository.findFollow(myId, targetId).isPresent();
    }

    /**
     * 랭킹
     */
//    public Page<RankingResponse> getRanking(String type, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        List<Member> members;
//        long totalCount;
//
//        if (type.equalsIgnoreCase("total")) {
//            members = memberRepository.findTopByTotalScore(pageable);
//            totalCount = memberRepository.countMembers(); // 전체 회원 수 조회
//        } else {
//            members = memberRepository.findTopByBaseScore(pageable);
//            totalCount = memberRepository.countMembers(); // 전체 회원 수 조회
//        }
//
//        List<RankingResponse> content = new ArrayList<>();
//        int rank = page * size + 1; // 페이지에 따른 시작 랭킹 계산
//        for (Member member : members) {
//            content.add(RankingResponse.create(rank++, member));
//        }
//
//        return new PageImpl<>(content, pageable, totalCount);
//    }
    public Page<RankingResponse> getRanking(String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Member> members;
        long totalCount;

        if (type.equalsIgnoreCase("total")) {
            members = memberRepository.findTopByTotalScore(pageable);
            totalCount = memberRepository.countMembers();
        } else {
            members = memberRepository.findTopByBaseScore(pageable);
            totalCount = memberRepository.countMembers();
        }

        List<RankingResponse> content = new ArrayList<>();
        int rank = page * size + 1;
        for (Member member : members) {
            int trend;

            if (member.isNewMember()) {
                // 신규 회원은 트렌드를 "NEW"로 표시하기 위해 특별한 값 설정
                // 예: -9999를 사용하여 프론트엔드에서 "NEW"로 표시
                trend = -9999; // 신규 회원 표시용 특별 값
            } else {
                // 기존 회원은 일반적인 트렌드 계산
                int previousRank;
                if (type.equalsIgnoreCase("total")) {
                    previousRank = member.getPreviousTotalRank();
                } else {
                    previousRank = member.getPreviousBaseRank();
                }

                trend = previousRank > 0 ? previousRank - rank : 0;
            }

            content.add(new RankingResponse(
                    rank,
                    member.getId(),
                    member.getAccount(),
                    member.getUserName(),
                    member.getProfileImageUrl(),
                    member.getTotalScore(),
                    member.getBaseScore(),
                    trend
            ));

            rank++;
        }

        return new PageImpl<>(content, pageable, totalCount);
    }

    public Page<RankingResponse> getFollowingRanking(Long memberId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // 현재 회원이 팔로우한 사용자들의 ID 목록 조회
        List<Long> followingIds = followRepository.findFollowings(memberId);

        // 자신도 랭킹에 포함 (선택사항, 원하는 경우 포함)
        followingIds.add(memberId);
        long totalCount = memberRepository.countFollowings(followingIds);
        List<Member> members = memberRepository.findFollowingsByBaseScore(followingIds, pageable);
        List<RankingResponse> content = new ArrayList<>();
        int rank = page * size + 1; // 페이지에 따른 시작 랭킹 계산
        for (Member member : members) {
            content.add(RankingResponse.create(rank++, member));
        }
        return new PageImpl<>(content, pageable, totalCount);
    }

    /**
     * 이전 랭킹 업데이트 0시 0분
     */
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void updateMemberRankings() {
        // 전체 회원을 랭킹별로 조회
        List<Member> membersByTotalScore = memberRepository.findAllOrderByTotalScoreDesc();
        List<Member> membersByBaseScore = memberRepository.findAllOrderByBaseScoreDesc();

        // 각 멤버의 현재 랭킹 정보 저장
        Map<Long, Integer> totalRankMap = new HashMap<>();
        Map<Long, Integer> baseRankMap = new HashMap<>();

        // 총점 기준 순위 맵 생성
        for (int i = 0; i < membersByTotalScore.size(); i++) {
            Member member = membersByTotalScore.get(i);
            totalRankMap.put(member.getId(), i + 1);
        }

        // 기본 점수 기준 순위 맵 생성
        for (int i = 0; i < membersByBaseScore.size(); i++) {
            Member member = membersByBaseScore.get(i);
            baseRankMap.put(member.getId(), i + 1);
        }

        // 각 멤버의 랭킹 정보 업데이트
        for (Member member : membersByTotalScore) {
            int totalRank = totalRankMap.getOrDefault(member.getId(), 0);
            int baseRank = baseRankMap.getOrDefault(member.getId(), 0);
            member.updateRankInfo(totalRank, baseRank);
            memberRepository.save(member);
        }
    }

    //테스트
    /**
     * 특정 날짜의 점수 갱신 이전에 이전 랭킹 정보 갱신
     * @param date 처리할 날짜
     */
    @Transactional
    public void updateMemberRankingsForDate(LocalDate date) {
        System.out.println("날짜 " + date + " 기준으로 랭킹 정보 업데이트 시작");

        // 1. 점수 기준으로 회원 목록 조회
        List<Member> membersByTotalScore = memberRepository.findAllOrderByTotalScoreDesc();
        List<Member> membersByBaseScore = memberRepository.findAllOrderByBaseScoreDesc();

        // 2. 순위 맵 생성
        Map<Long, Integer> totalRankMap = new HashMap<>();
        Map<Long, Integer> baseRankMap = new HashMap<>();

        for (int i = 0; i < membersByTotalScore.size(); i++) {
            Member member = membersByTotalScore.get(i);
            totalRankMap.put(member.getId(), i + 1);
        }

        for (int i = 0; i < membersByBaseScore.size(); i++) {
            Member member = membersByBaseScore.get(i);
            baseRankMap.put(member.getId(), i + 1);
        }

        // 3. 각 멤버의 랭킹 정보 업데이트
        for (Member member : membersByTotalScore) {
            int totalRank = totalRankMap.getOrDefault(member.getId(), 0);
            int baseRank = baseRankMap.getOrDefault(member.getId(), 0);
            member.updateRankInfo(totalRank, baseRank);
            memberRepository.save(member);
        }

        System.out.println("날짜 " + date + " 기준 랭킹 정보 업데이트 완료");
    }
}