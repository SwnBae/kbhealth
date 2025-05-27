package kb.health.controller;

import kb.health.authentication.CurrentMember;
import kb.health.authentication.LoginMember;
import kb.health.controller.request.GptFoodRecommendRequest;
import kb.health.controller.request.GptHealthCheckRequest;
import kb.health.controller.response.DailyNutritionAchievementResponse;
import kb.health.controller.response.GptFoodRecommendResponse;
import kb.health.controller.response.GptHealthCheckResponse;
import kb.health.controller.response.NutritionAchievementResponse;
import kb.health.domain.Member;
import kb.health.service.DailyNutritionAchievementService;
import kb.health.service.GptService;
import kb.health.service.MemberService;
import kb.health.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gpt")
public class GptController {

    private final MemberService memberService;
    private final RecordService recordService;
    private final GptService gptService;
    private final DailyNutritionAchievementService dailyNutritionAchievementService;

    @PostMapping("/recommendDiet")
    public ResponseEntity<GptFoodRecommendResponse> recommendFood(@LoginMember CurrentMember currentMember) {
        Member member = memberService.findById(currentMember.getId());

        NutritionAchievementResponse nutritionAchievementResponse = recordService.getNutritionAchievement(member.getId(), LocalDate.now());

        GptFoodRecommendRequest request = GptFoodRecommendRequest.create(member, nutritionAchievementResponse);
        GptFoodRecommendResponse response = gptService.getFoodRecommendation(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/healthCheck")
    public ResponseEntity<GptHealthCheckResponse> checkHealth(@LoginMember CurrentMember currentMember) {
        Member member = memberService.findById(currentMember.getId());

        // 최근 10일간의 영양 달성률 조회
        List<DailyNutritionAchievementResponse> nutritionData =
                dailyNutritionAchievementService.getLast10DaysNutritionResponses(member);

        // 데이터가 부족한 경우 처리
        if (nutritionData.size() < 3) {
            return ResponseEntity.ok(new GptHealthCheckResponse(
                    "데이터 부족",
                    "건강상태 분석을 위한 충분한 영양 데이터가 없습니다.",
                    "최소 3일 이상의 식단 기록이 필요합니다.",
                    "더 많은 식단 데이터를 입력해주세요."
            ));
        }

        GptHealthCheckRequest request = GptHealthCheckRequest.create(member, nutritionData);
        GptHealthCheckResponse response = gptService.getHealthCheck(request);

        return ResponseEntity.ok(response);
    }

}
