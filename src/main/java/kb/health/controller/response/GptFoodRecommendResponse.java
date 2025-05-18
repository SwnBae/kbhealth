package kb.health.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GptFoodRecommendResponse {
    private String recommendation;  // GPT가 생성한 추천 음식 텍스트
}
