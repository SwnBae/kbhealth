package kb.health.controller;

import kb.health.Service.MemberService;
import kb.health.authentication.CurrentMember;
import kb.health.authentication.JwtUtil;
import kb.health.authentication.LoginMember;
import kb.health.controller.response.FollowResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follow")
public class FollowController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    @PostMapping("/following/{member_id}")
    public ResponseEntity<?> followMember(@LoginMember CurrentMember currentMember, @PathVariable("member_id") Long followingId) {
        memberService.follow(currentMember.getId(), followingId);
        return ResponseEntity.ok("Success");
    }

    @DeleteMapping("/following/{member_id}")
    public ResponseEntity<?> unfollowMember(@LoginMember CurrentMember currentMember, @PathVariable("member_id") Long memberId) {
        memberService.unfollow(currentMember.getId(), memberId);
        return ResponseEntity.ok("Success");
    }

    /**
     * 팔로잉 목록 조회 API
     */
    @GetMapping("/followingList/{member_id}")
    public ResponseEntity<List<FollowResponse>> getFollowings(@PathVariable("member_id") Long memberId){
        return ResponseEntity.ok(memberService.getFollowings(memberId));
    }

    /**
     * 팔로워 목록 조회 API
     */
    @GetMapping("/followerList/{member_id}")
    public ResponseEntity<List<FollowResponse>> getFollowers(@PathVariable("member_id") Long memberId){
        return ResponseEntity.ok(memberService.getFollowers(memberId));
    }
}
