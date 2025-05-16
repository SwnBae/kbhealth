package kb.health.controller.response;

import jakarta.persistence.Column;
import kb.health.domain.record.DietRecord;
import kb.health.domain.record.MealType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DietRecordResponse {
    private Long id;
    private Long dietId;
    private String dietMenu;
    private String category;
    private MealType mealType;
    private double amount;
    private int calories;
    private double protein;
    private double fat;
    private double carbohydrates;
    private double sugars;
    private double fiber;
    private double sodium;
    private String drImgUrl;
    private LocalDateTime lastModifyDate;

    public static DietRecordResponse create(DietRecord dietRecord) {
        Long id = dietRecord.getId();
        Long dietId = null;
        String dietMenu = "No menu available";
        String category = "";
        MealType mealType = dietRecord.getMealType();
        double amount = 0;
        int calories = 0;
        double protein = 0;
        double fat = 0;
        double carbohydrates = 0;
        double sugars = 0;
        double fiber = 0;
        double sodium = 0;
        String drImgUrl = null;
        LocalDateTime lastModifyDate = null;

        if (dietRecord.getDiet() != null) {
            dietId = dietRecord.getDiet().getId();
            dietMenu = dietRecord.getDiet().getMenu();
            category = dietRecord.getDiet().getCategory();
            amount = dietRecord.getAmount();

            // 영양성분 계산 (Diet는 100g/ml 기준, amount는 실제 섭취량)
            double ratio = amount / 100.0; // 100g/ml 대비 실제 섭취량의 비율

            // 각 영양성분 계산
            calories = (int) Math.round(dietRecord.getDiet().getCalories() * ratio);
            protein = roundToTwoDecimals(dietRecord.getDiet().getProtein() * ratio);
            fat = roundToTwoDecimals(dietRecord.getDiet().getFat() * ratio);
            carbohydrates = roundToTwoDecimals(dietRecord.getDiet().getCarbohydrates() * ratio);
            sugars = roundToTwoDecimals(dietRecord.getDiet().getSugars() * ratio);
            fiber = roundToTwoDecimals(dietRecord.getDiet().getFiber() * ratio);
            sodium = roundToTwoDecimals(dietRecord.getDiet().getSodium() * ratio);

            lastModifyDate = dietRecord.getLastModifyDate();
            drImgUrl = dietRecord.getDrImgUrl();
        }

        return new DietRecordResponse(
                id, dietId, dietMenu, category, mealType, amount, calories,
                protein, fat, carbohydrates, sugars, fiber, sodium,
                drImgUrl, lastModifyDate
        );
    }

    // 소수점 둘째 자리까지 반올림하는 유틸리티 메서드
    private static double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}