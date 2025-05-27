package kb.health.controller.response;

import kb.health.domain.DailyNutritionStandard;
import kb.health.domain.record.Diet;
import kb.health.domain.record.DietRecord;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * 영양소 달성 정도를 반환
 */

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NutritionAchievementResponse {

    private double caloriesRate;
    private double proteinRate;
    private double fatRate;
    private double carbRate;
    private double sugarsRate;
    private double fiberRate;
    private double sodiumRate;

    public static NutritionAchievementResponse create(List<DietRecord> records, DailyNutritionStandard standard) {
        double totalCalories = 0;
        double totalProtein = 0;
        double totalFat = 0;
        double totalCarbs = 0;
        double totalSugars = 0;
        double totalFiber = 0;
        double totalSodium = 0;

        for (DietRecord record : records) {
            Diet diet = record.getDiet();
            if (diet == null) continue;

            double factor = record.getAmount() / 100.0; // 100g 기준으로 비례 계산

            totalCalories += diet.getCalories() * factor;
            totalProtein += diet.getProtein() * factor;
            totalFat += diet.getFat() * factor;
            totalCarbs += diet.getCarbohydrates() * factor;
            totalSugars += diet.getSugars() * factor;
            totalFiber += diet.getFiber() * factor;
            totalSodium += diet.getSodium() * factor;
        }

        return new NutritionAchievementResponse(
                rate(totalCalories, standard.getCalories()),
                rate(totalProtein, standard.getProtein()),
                rate(totalFat, standard.getFat()),
                rate(totalCarbs, standard.getCarbohydrates()),
                rate(totalSugars, standard.getSugars()),
                rate(totalFiber, standard.getFiber()),
                rate(totalSodium, standard.getSodium())
        );
    }

    private static double rate(double intake, double standard) {
        if (standard == 0) return 0;
        return intake / standard; // Math.min 제거하여 100% 초과 허용
    }
}

