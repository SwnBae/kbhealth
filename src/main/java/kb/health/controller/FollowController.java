package kb.health.controller;

import kb.health.Service.MemberService;
import kb.health.authentication.CurrentMember;
import kb.health.authentication.JwtUtil;
import kb.health.authentication.LoginMember;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FollowController {
    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    @PostMapping("/profile/{member_id}/follow")
    public String follow(@PathVariable("member_id") Long followingId , @LoginMember CurrentMember currentMember) {
        memberService.follow(currentMember.getId(), followingId);
        return "succeed";
    }


}
