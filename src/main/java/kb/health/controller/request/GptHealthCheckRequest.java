package kb.health.controller.request;

import kb.health.controller.response.DailyNutritionAchievementResponse;
import kb.health.domain.Gender;
import kb.health.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GptHealthCheckRequest {

    private int age;
    private Gender gender;
    private double height;
    private double weight;
    private List<DailyNutritionAchievementResponse> last10DaysNutrition;

    public static GptHealthCheckRequest create(Member member, List<DailyNutritionAchievementResponse> nutritionData) {
        return new GptHealthCheckRequest(
                member.getBodyInfo().getAge(),
                member.getBodyInfo().getGender(),
                member.getBodyInfo().getHeight(),
                member.getBodyInfo().getWeight(),
                nutritionData
        );
    }
}