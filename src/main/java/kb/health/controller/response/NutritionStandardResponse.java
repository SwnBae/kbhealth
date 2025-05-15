package kb.health.controller.response;

import kb.health.domain.DailyNutritionStandard;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NutritionStandardResponse {
    private int calories;

    private double protein;
    private double fat;
    private double carbohydrates;
    private double sugars;
    private double fiber;
    private double sodium;

    public static NutritionStandardResponse create(DailyNutritionStandard dailyNutritionStandard){
        NutritionStandardResponse nutritionStandardResponse = new NutritionStandardResponse();

        nutritionStandardResponse.calories = dailyNutritionStandard.getCalories();

        nutritionStandardResponse.protein = dailyNutritionStandard.getProtein();
        nutritionStandardResponse.fat = dailyNutritionStandard.getFat();
        nutritionStandardResponse.carbohydrates = dailyNutritionStandard.getCarbohydrates();
        nutritionStandardResponse.sugars = dailyNutritionStandard.getSugars();
        nutritionStandardResponse.fiber = dailyNutritionStandard.getFiber();
        nutritionStandardResponse.sodium = dailyNutritionStandard.getSodium();

        return nutritionStandardResponse;
    }
}
