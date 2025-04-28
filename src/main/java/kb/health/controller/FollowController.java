package kb.health.controller;

import kb.health.Service.MemberService;
import kb.health.authentication.CurrentMember;
import kb.health.authentication.JwtUtil;
import kb.health.authentication.LoginMember;
import kb.health.domain.response.FollowResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follow")
public class FollowController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    @PostMapping("/following/{member_id}")
    public String follow(@PathVariable("member_id") Long followingId , @LoginMember CurrentMember currentMember) {
        memberService.follow(currentMember.getId(), followingId);
        return "succeed";
    }

    /**
     * 팔로잉 목록 조회 API
     */
    @GetMapping("/followingList/{member_id}")
    public List<FollowResponse> getFollowings(@PathVariable("member_id") Long memberId) {
        return memberService.getFollowings(memberId);
    }

    /**
     * 팔로워 목록 조회 API
     */
    @GetMapping("/followerList/{member_id}")
    public List<FollowResponse> getFollowers(@PathVariable("member_id") Long memberId) {
        return memberService.getFollowers(memberId);
    }

    @PostMapping("/unfollow/{member_id}")
    public String unfollow(@PathVariable("member_id") Long followingId, @LoginMember CurrentMember currentMember) {
        memberService.unfollow(currentMember.getId(), followingId);
        return "succeed";
    }
}
