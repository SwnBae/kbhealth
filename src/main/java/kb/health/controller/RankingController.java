package kb.health.controller;

import kb.health.authentication.CurrentMember;
import kb.health.authentication.LoginMember;
import kb.health.controller.response.RankingResponse;
import kb.health.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ranking")
public class RankingController {

    private final MemberService memberService;

    // 랭킹 조회 API
    @GetMapping
    public ResponseEntity<Page<RankingResponse>> getRanking(
            @RequestParam String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<RankingResponse> ranking = memberService.getRanking(type, page, size);
        return ResponseEntity.ok(ranking);
    }

    @GetMapping("/following")
    public ResponseEntity<Page<RankingResponse>> getFollowingRanking(
            @LoginMember CurrentMember currentMember,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<RankingResponse> ranking = memberService.getFollowingRanking(
                currentMember.getId(), page, size);
        return ResponseEntity.ok(ranking);
    }
}
