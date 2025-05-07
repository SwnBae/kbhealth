package kb.health.controller.response;

import kb.health.domain.record.DietRecord;
import kb.health.domain.record.MealType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class DietRecordResponse {
    private Long id;
    private Long dietId;
    private String dietMenu;
    private MealType mealType;

    public static DietRecordResponse create(DietRecord dietRecord) {
        Long id = dietRecord.getId();
        Long dietId = null;
        String dietMenu = "No menu available";

        if (dietRecord.getDiet() != null) {
            dietId = dietRecord.getDiet().getId();
            dietMenu = dietRecord.getDiet().getMenu();
        }

        return new DietRecordResponse(id, dietId, dietMenu, dietRecord.getMealType());
    }
}
