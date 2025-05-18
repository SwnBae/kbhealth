package kb.health.controller;

import kb.health.authentication.CurrentMember;
import kb.health.authentication.LoginMember;
import kb.health.controller.request.GptFoodRecommendRequest;
import kb.health.controller.response.GptFoodRecommendResponse;
import kb.health.controller.response.NutritionAchievementResponse;
import kb.health.domain.Member;
import kb.health.service.GptService;
import kb.health.service.MemberService;
import kb.health.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gpt")
public class GptController {

    private final MemberService memberService;
    private final RecordService recordService;
    private final GptService gptService;

    @PostMapping("/recommendDiet")
    public ResponseEntity<GptFoodRecommendResponse> recommendFood(@LoginMember CurrentMember currentMember) {
        Member member = memberService.findById(currentMember.getId());

        NutritionAchievementResponse nutritionAchievementResponse = recordService.getNutritionAchievement(member.getId(), LocalDate.now());

        GptFoodRecommendRequest request = GptFoodRecommendRequest.create(member, nutritionAchievementResponse);
        GptFoodRecommendResponse response = gptService.getFoodRecommendation(request);
        return ResponseEntity.ok(response);
    }

}
