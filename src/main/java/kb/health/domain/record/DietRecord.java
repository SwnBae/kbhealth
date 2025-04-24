package kb.health.domain.record;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class DietRecord extends BaseRecord {

    private String menu;
    private int totalCalories;

    @Enumerated(EnumType.STRING)
    private MealType mealType; // 아침/점심/저녁
}

