package kb.health.service;

import kb.health.client.GptClient;
import kb.health.controller.request.GptFoodRecommendRequest;
import kb.health.controller.request.GptHealthCheckRequest;
import kb.health.controller.response.DailyNutritionAchievementResponse;
import kb.health.controller.response.GptFoodRecommendResponse;
import kb.health.controller.response.GptHealthCheckResponse;
import kb.health.domain.Gender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GptService {

    private final GptClient gptClient;

    public GptFoodRecommendResponse getFoodRecommendation(GptFoodRecommendRequest request) {
        String prompt = buildPrompt(request);
        String answer = gptClient.askGpt(prompt);
        return new GptFoodRecommendResponse(answer);
    }

    public GptHealthCheckResponse getHealthCheck(GptHealthCheckRequest request) {
        String prompt = createHealthCheckPrompt(request);

        try {
            // 기존 GptClient 사용
            String response = gptClient.askGpt(prompt);
            return new GptHealthCheckResponse(
                    "분석 완료",
                    response,
                    "위 분석을 참고하여 건강한 식습관을 유지하세요.",
                    null
            );
        } catch (Exception e) {
            return new GptHealthCheckResponse(
                    "분석 불가",
                    "현재 건강상태 분석이 불가능합니다.",
                    "잠시 후 다시 시도해주세요.",
                    null
            );
        }
    }

    private String createHealthCheckPrompt(GptHealthCheckRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("너는 '핏버니'라는 이름의 토끼 영양사야. ");
        prompt.append("사용자의 건강을 진심으로 걱정하고, 친근하면서도 전문적인 조언을 해줘. ");
        prompt.append("말끝에 '~해요', '~네요' 같은 정중하고 귀여운 말투를 사용해. ");
        prompt.append("가끔 '🥕'나 '🐰' 같은 이모티콘도 사용해도 좋아.\n\n");
        prompt.append("사용자의 기본 정보와 최근 10일간의 영양소 달성률을 보고 건강상태를 분석해줘.\n\n");

        // 기본 정보
        prompt.append("=== 사용자 정보 ===\n");
        prompt.append("나이: ").append(request.getAge()).append("세\n");
        prompt.append("성별: ").append(formatGender(request.getGender())).append("\n");
        prompt.append("신장: ").append(request.getHeight()).append("cm\n");
        prompt.append("체중: ").append(request.getWeight()).append("kg\n\n");

        // 영양 달성률 데이터
        prompt.append("=== 최근 10일간 영양소 달성률 ===\n");
        prompt.append("(1.0 = 100% 달성, 0.5 = 50% 달성)\n");

        for (DailyNutritionAchievementResponse nutrition : request.getLast10DaysNutrition()) {
            prompt.append(nutrition.getDate())
                    .append(" - 칼로리: ").append(String.format("%.2f", nutrition.getCaloriesRate()))
                    .append(", 단백질: ").append(String.format("%.2f", nutrition.getProteinRate()))
                    .append(", 지방: ").append(String.format("%.2f", nutrition.getFatRate()))
                    .append(", 탄수화물: ").append(String.format("%.2f", nutrition.getCarbRate()))
                    .append(", 섬유질: ").append(String.format("%.2f", nutrition.getFiberRate()))
                    .append(", 나트륨: ").append(String.format("%.2f", nutrition.getSodiumRate()))
                    .append("\n");
        }

        prompt.append("\n위 데이터를 바탕으로 전반적인 건강상태, 영양상태 분석, 개선 권장사항을 알려줘. (200자 이내, !!!줄바꿈이나 띄어쓰기 자주 써줘 매우 중요!!!, 그리고 마지막 건강 챙기라는 말은 무조건 줄바꿈 후에 작성해줘)");

        System.out.println("==== Health Check GPT Prompt ====");
        System.out.println(prompt);
        System.out.println("=================================");

        return prompt.toString();
    }


    private String buildPrompt(GptFoodRecommendRequest req) {
        String prompt = String.format("""
        너는 '핏버니'라는 이름의 토끼 영양사야. 말끝에 '~해요', '~네요' 같은 정중하고 귀여운 말투를 사용해.
        가끔 '🥕'나 '🐰' 같은 이모티콘도 사용해도 좋아.
        사용자 신체 정보와 오늘 섭취한 영양소를 보고, 부족하거나 과한 영양소에 맞게 먹을 요리를 추천해줘.

        [사용자 정보]
        - 키: %.1f cm
        - 몸무게: %.1f kg
        - 나이: %d세
        - 성별: %s

        [오늘 섭취한 영양소 (하루 권장량 대비 섭취율)]
        - 칼로리: %.1f%%
        - 단백질: %.1f%%
        - 지방: %.1f%%
        - 탄수화물: %.1f%%
        - 당류: %.1f%%
        - 섬유질: %.1f%%
        - 나트륨: %.1f%%

        부족하거나 과다 섭취된 영양소를 기준으로, 2~3가지 실제 요리 이름(구체적으로: ex. 피자, 파스타, 김치찌개)으로 추천해줘. 그리고 추천 이유도 함께 알려줘. (200자 이내)
        """,
                req.getHeight(), req.getWeight(), req.getAge(), formatGender(req.getGender()),
                req.getCalories() * 100, req.getProtein() * 100, req.getFat() * 100, req.getCarbohydrate() * 100,
                req.getSugars() * 100, req.getFiber() * 100, req.getSodium() * 100);

        System.out.println("==== GPT Prompt ====");
        System.out.println(prompt);
        System.out.println("====================");

        return prompt;
    }

    private String formatGender(Gender gender) {
        return switch (gender) {
            case MALE -> "남성";
            case FEMALE -> "여성";
        };
    }
}
