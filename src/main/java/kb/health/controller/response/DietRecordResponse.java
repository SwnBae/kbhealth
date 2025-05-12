package kb.health.controller.response;

import kb.health.domain.record.DietRecord;
import kb.health.domain.record.MealType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@ToString
public class DietRecordResponse {
    private Long id;
    private Long dietId;
    private String dietMenu;
    private double amount;
    private LocalDateTime lastModifyDate;
    private MealType mealType;

    public static DietRecordResponse create(DietRecord dietRecord) {
        Long id = dietRecord.getId();
        Long dietId = null;
        String dietMenu = "No menu available";
        double amount = 0;
        LocalDateTime lastModifyDate = null;

        if (dietRecord.getDiet() != null) {
            dietId = dietRecord.getDiet().getId();
            dietMenu = dietRecord.getDiet().getMenu();
            amount = dietRecord.getAmount();
            lastModifyDate = dietRecord.getLastModifyDate();
        }

        return new DietRecordResponse(id, dietId, dietMenu, amount, lastModifyDate, dietRecord.getMealType());
    }
}
