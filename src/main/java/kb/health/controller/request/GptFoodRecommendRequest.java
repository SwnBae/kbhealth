package kb.health.controller.request;

import kb.health.controller.response.NutritionAchievementResponse;
import kb.health.domain.Gender;
import kb.health.domain.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class GptFoodRecommendRequest {
    private double height;        // cm
    private double weight;        // kg
    private int age;
    private Gender gender;        // "MALE" / "FEMALE"

    private double calories;      // kcal
    private double protein;       // g
    private double fat;           // g
    private double carbohydrate;  // g
    private double sugars;        // g
    private double fiber;         // g
    private double sodium;        // mg

    public static GptFoodRecommendRequest create(Member member, NutritionAchievementResponse response) {
        GptFoodRecommendRequest request = new GptFoodRecommendRequest();
        request.setHeight(member.getBodyInfo().getHeight());
        request.setWeight(member.getBodyInfo().getWeight());
        request.setAge(member.getBodyInfo().getAge());
        request.setGender(member.getBodyInfo().getGender());

        request.setCalories(response.getCaloriesRate());
        request.setProtein(response.getProteinRate());
        request.setFat(response.getFatRate());
        request.setCarbohydrate(response.getCarbRate());
        request.setSugars(response.getSugarsRate());
        request.setFiber(response.getFiberRate());
        request.setSodium(response.getSodiumRate());

        return request;
    }
}
