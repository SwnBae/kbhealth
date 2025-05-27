package kb.health.controller;

import kb.health.domain.notification.NotificationType;
import kb.health.service.MemberService;
import kb.health.authentication.CurrentMember;
import kb.health.authentication.JwtUtil;
import kb.health.authentication.LoginMember;
import kb.health.controller.response.FollowResponse;
import kb.health.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follow")
public class FollowController {

    private final MemberService memberService;
    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;

    @PostMapping("/following/{member_id}")
    public ResponseEntity<?> followMember(@LoginMember CurrentMember currentMember, @PathVariable("member_id") Long followingId) {
        memberService.follow(currentMember.getId(), followingId);
        notificationService.createFollowNotification(currentMember.getId(), followingId);

        return ResponseEntity.ok("Success");
    }

    @DeleteMapping("/following/{member_id}")
    public ResponseEntity<?> unfollowMember(@LoginMember CurrentMember currentMember, @PathVariable("member_id") Long memberId) {
        memberService.unfollow(currentMember.getId(), memberId);
        notificationService.deleteNotification(currentMember.getId(), memberId, NotificationType.FOLLOW, null);
        return ResponseEntity.ok("Success");
    }

    /**
     * 팔로잉 목록 조회 API
     */
    @GetMapping("/followingList/{member_id}")
    public ResponseEntity<List<FollowResponse>> getFollowings(@PathVariable("member_id") Long memberId){

        // ✅ 최적화: JOIN FETCH를 사용한 한번의 쿼리로 모든 데이터 조회
        List<FollowResponse> followings = memberService.getFollowings(memberId);

        return ResponseEntity.ok(followings);
    }

    /**
     * 팔로워 목록 조회 API
     */
    @GetMapping("/followerList/{member_id}")
    public ResponseEntity<List<FollowResponse>> getFollowers(@PathVariable("member_id") Long memberId){

        // ✅ 최적화: JOIN FETCH를 사용한 한번의 쿼리로 모든 데이터 조회
        List<FollowResponse> followers = memberService.getFollowers(memberId);

        return ResponseEntity.ok(followers);
    }

}
