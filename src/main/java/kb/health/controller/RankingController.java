package kb.health.controller;

import kb.health.controller.response.RankingResponse;
import kb.health.service.MemberService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<RankingResponse>> getRanking(
            @RequestParam String type,
            @RequestParam int limit) {
        List<RankingResponse> ranking = memberService.getRanking(type, limit);

        for(RankingResponse rankingResponse : ranking){
            System.out.println(rankingResponse);
        }

        return ResponseEntity.ok(ranking);
    }
}
