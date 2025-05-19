package kb.health.service;

import kb.health.client.GptClient;
import kb.health.controller.request.GptFoodRecommendRequest;
import kb.health.controller.response.GptFoodRecommendResponse;
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

    private String buildPrompt(GptFoodRecommendRequest req) {
        String prompt = String.format("""
        너는 영양사야. 사용자 신체 정보와 오늘 섭취한 영양소를 보고,
        부족하거나 과한 영양소에 맞게 먹을 요리를 추천해줘.

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
