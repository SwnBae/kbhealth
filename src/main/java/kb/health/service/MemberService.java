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

        // 이름 중복 확인
        memberRepository.findByUserName(request.getUserName())
                .ifPresent(m -> {
                    throw MemberException.duplicateUserName();
                });

        // 계정 중복 확인
        memberRepository.findByAccount(request.getAccount())
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

        // 이름 중복 확인
        memberRepository.findByUserName(member.getUserName())
                .ifPresent(m -> {
                    throw MemberException.duplicateUserName();
                });

        // 계정 중복 확인
        memberRepository.findByAccount(member.getAccount())
                .ifPresent(m -> {
                    throw MemberException.duplicateAccount();
                });

        memberRepository.save(member);
        return member.getId();
    }

    // 아이디 중복 검사
    public boolean isAccountDuplicate(String account) {
        return memberRepository.findByAccount(account).isPresent();
    }

    // 닉네임 중복 검사
    public boolean isUsernameDuplicate(String userName) {
        return memberRepository.findByUserName(userName).isPresent();
    }

    /**
     * 회원 수정
     */
    //계정 정보 수정
    @Transactional
    public void updateMember(Long memberId, MemberEditRequest memberEditRequest) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        member.updateAccountInfo(memberEditRequest);
    }

    //신체 정보 수정
    @Transactional
    public void updateMemberBodyInfo(Long memberId, MemberBodyInfoEditRequest bodyInfoEditRequest) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        member.updateBodyInfo(bodyInfoEditRequest);
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


    // 닉네임으로 찾기
    public Member findMemberByUserName(String userName) {
        return memberRepository.findByUserName(userName)
                .orElseThrow(() -> MemberException.memberNotFoundByUserName());
    }

    // 계정으로 찾기
    public Member findMemberByAccount(String account) {
        return memberRepository.findByAccount(account)
                .orElseThrow(() -> MemberException.memberNotFoundByAccount());
    }


    // 유저 검색 (Account, userName)
    public List<Member> searchByUserNameOrAccountLike(String keyword) {
        return memberRepository.findByUserNameOrAccountContaining(keyword);
    }

    /**
     * 아래의 멤버 찾는 메서드는 내부 로직에서 사용할 예비 메서드
     */
    public Member getMemberByAccount(String account) {
        return memberRepository.findByAccount(account)
                .orElseThrow(() -> MemberException.memberNotFoundByAccount());
    }

    //고유 값(PK)로 찾기, 내부 로직 사용
    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
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
        if (followRepository.existsByFromIdAndToId(myId, targetId)) {
//            throw FollowException.alreadyFollowing(); // 이미 팔로우 중인 경우 예외 발생
        }

        //영속화
        Member me = memberRepository.findById(myId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        Member target = memberRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // 팔로우 객체 생성
        Follow follow = Follow.createFollow(me, target);

        // 팔로우 엔티티 저장
        followRepository.save(follow);
    }

    //팔로우 취소
    @Transactional
    public void unfollow(Long myId, Long targetId) {
        Follow follow = followRepository.findByFromIdAndToId(myId, targetId)
                .orElseThrow(() -> FollowException.followNotFound());

        follow.disconnect();

        followRepository.delete(follow);
    }

    // ✅ 메서드 이름만으로 COUNT 쿼리 실행!
    public int getFollowingCount(Long memberId) {
        return followRepository.countByFromId(memberId);  // 매우 간단!
    }

    public int getFollowerCount(Long memberId) {
        return followRepository.countByToId(memberId);    // 매우 간단!
    }

    public List<FollowResponse> getFollowings(Long memberId) {
        List<Follow> follows = followRepository.findFollowingsWithMembers(memberId);
        return follows.stream()
                .map(follow -> FollowResponse.create(follow.getTo()))
                .toList();
    }

    public List<FollowResponse> getFollowers(Long memberId) {
        List<Follow> follows = followRepository.findFollowersWithMembers(memberId);
        return follows.stream()
                .map(follow -> FollowResponse.create(follow.getFrom()))
                .toList();
    }

    public boolean isFollowing(Long myId, Long targetId) {
        return followRepository.findByFromIdAndToId(myId, targetId).isPresent();
    }

    /**
     * 랭킹
     */
    public Page<RankingResponse> getRanking(String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Member> members;
        long totalCount;

        if (type.equalsIgnoreCase("total")) {
            members = memberRepository.findAllByOrderByTotalScoreDesc(pageable);
            totalCount = memberRepository.count();
        } else {
            members = memberRepository.findAllByOrderByBaseScoreDesc(pageable);
            totalCount = memberRepository.count();
        }

        List<RankingResponse> content = new ArrayList<>();
        int rank = page * size + 1;
        for (Member member : members) {
            int trend;

            if (member.isNewMember()) {
                // 신규 회원은 트렌드를 "NEW"로 표시하기 위해 특별한 값(-9999) 설정
                trend = -9999;
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
        List<Long> followingIds = followRepository.findFollowingIds(memberId);

        // 자신도 랭킹에 포함 (선택사항, 원하는 경우 포함)
        followingIds.add(memberId);
        long totalCount = memberRepository.countByIdIn(followingIds);
        List<Member> members = memberRepository.findByIdInOrderByBaseScoreDesc(followingIds, pageable);
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
        List<Member> membersByTotalScore = memberRepository.findAllByOrderByTotalScoreDesc();
        List<Member> membersByBaseScore = memberRepository.findAllByOrderByBaseScoreDesc();

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
        List<Member> membersByTotalScore = memberRepository.findAllByOrderByTotalScoreDesc();
        List<Member> membersByBaseScore = memberRepository.findAllByOrderByBaseScoreDesc();

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