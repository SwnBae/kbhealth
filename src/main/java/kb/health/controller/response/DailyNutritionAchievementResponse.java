package kb.health.controller.response;

import kb.health.domain.DailyNutritionAchievement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DailyNutritionAchievementResponse {

    private LocalDate date;
    private double caloriesRate;
    private double proteinRate;
    private double fatRate;
    private double carbRate;
    private double sugarsRate;
    private double fiberRate;
    private double sodiumRate;

    // 정적 팩토리 메서드
    public static DailyNutritionAchievementResponse create(DailyNutritionAchievement achievement) {
        return new DailyNutritionAchievementResponse(
                achievement.getDate(),
                achievement.getCaloriesRate(),
                achievement.getProteinRate(),
                achievement.getFatRate(),
                achievement.getCarbRate(),
                achievement.getSugarsRate(),
                achievement.getFiberRate(),
                achievement.getSodiumRate()
        );
    }
}