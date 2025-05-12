package kb.health.controller;

import kb.health.controller.request.MemberBodyInfoEditRequest;
import kb.health.controller.response.MemberSearchResponse;
import kb.health.service.MemberService;
import kb.health.service.RecordService;
import kb.health.service.ScoreService;
import kb.health.authentication.CurrentMember;
import kb.health.authentication.LoginMember;
import kb.health.controller.request.MemberEditRequest;
import kb.health.controller.response.DailyScoreResponse;
import kb.health.controller.response.MemberProfileResponse;
import kb.health.controller.response.NutritionAchievementResponse;
import kb.health.domain.DailyScore;
import kb.health.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {

    private final MemberService memberService;
    private final RecordService recordService;
    private final ScoreService scoreService;

    //프로필 조회
    @GetMapping("/{member_account}")
    public ResponseEntity<MemberProfileResponse> getProfile(@LoginMember CurrentMember currentMember, @PathVariable("member_account") String member_account) {
        System.out.println("프로필 조회");
        // 1. 멤버 가져오기
        Member member = memberService.findMemberByAccount(member_account);

        // 2. 오늘의 영양성분 달성 정도 체크
        NutritionAchievementResponse nutritionAchievementResponse = recordService.getNutritionAchievement(member.getId(), LocalDate.now());

        int followingCount = memberService.getFollowings(member.getId()).size(); // 팔로잉 수
        int followerCount = memberService.getFollowers(member.getId()).size();   // 팔로워 수

        // 3. 최근 10일 점수 조회 및 변환
        List<DailyScore> last10Scores = scoreService.getLast10DaysScores(member);
        List<DailyScoreResponse> last10ScoreResponses = last10Scores.stream()
                .map(DailyScoreResponse::create)
                .toList();

        // 4. MemberResponse 생성
        MemberProfileResponse memberProfileResponse = MemberProfileResponse.create(member, nutritionAchievementResponse, last10ScoreResponses,followingCount,followerCount);

        Member follow = memberService.getMemberByAccount(member_account);

        memberProfileResponse.setFollowing(memberService.isFollowing(currentMember.getId(), follow.getId()));

        return ResponseEntity.ok(memberProfileResponse);
    }

    // 이미지 주소 요청
//    @GetMapping("/profile/{member_account}/image")
//    public ResponseEntity<Member> getProfileImage(@PathVariable("member_account") String member_account) {
//
//    }

    // 프로필 수정
    @PostMapping("/editinfo")
    public ResponseEntity<?> updateProfile(@LoginMember CurrentMember currentMember, @RequestBody MemberEditRequest memberEditRequest) {
        memberService.updateMember(currentMember.getId(), memberEditRequest);
        return ResponseEntity.ok("회원정보 수정 성공");
    }

    @PostMapping("/editbodyinfo")
    public  ResponseEntity<?> updateBodyInfo(@LoginMember CurrentMember currentMember, @RequestBody MemberBodyInfoEditRequest memberBodyInfoEditRequest) {
        memberService.updateMemberBodyInfo(currentMember.getId(), memberBodyInfoEditRequest);
        return ResponseEntity.ok("신체정보 수정 성공");
    }

    // 유저 검색
    @GetMapping("/members/search")
    public ResponseEntity<List<MemberSearchResponse>> searchMembers(@RequestParam String keyword) {
        List<Member> members = memberService.searchByUserNameOrAccountLike(keyword);
        List<MemberSearchResponse> searchList = members.stream()
                .map(MemberSearchResponse::create)
                .toList();

        return ResponseEntity.ok(searchList);
    }

}
