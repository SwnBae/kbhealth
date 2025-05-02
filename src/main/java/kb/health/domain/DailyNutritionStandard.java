package kb.health.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static java.lang.Math.round;

@Embeddable
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DailyNutritionStandard {

    @Column(nullable = false)
    private int calories;

    private double protein;
    private double fat;
    private double carbohydrates;
    private double sugars;
    private double fiber;
    private double sodium;

    public static DailyNutritionStandard calculate(BodyInfo bodyInfo) {
        double weight = bodyInfo.getWeight();
        double height = bodyInfo.getHeight();
        int age = bodyInfo.getAge();
        Gender gender = bodyInfo.getGender();

        double bmr;
        if (gender == Gender.MALE) {
            bmr = 66.47 + (13.75 * weight) + (5.003 * height) - (6.755 * age);
        } else {
            bmr = 655.1 + (9.563 * weight) + (1.850 * height) - (4.676 * age);
        }

        double totalCalories = bmr * 1.2;
        double protein = weight * 1.2;
        double fat = (totalCalories * 0.25) / 9;
        double carb = (totalCalories - (protein * 4) - (fat * 9)) / 4;

        return DailyNutritionStandard.builder()
                .calories((int) totalCalories)
                .protein(round(protein))
                .fat(round(fat))
                .carbohydrates(round(carb))
                .sugars(30)
                .fiber(25)
                .sodium(2000)
                .build();
    }
}

