package kb.health.domain.response;

import kb.health.domain.record.DietRecord;
import kb.health.domain.record.MealType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DietRecordResponse {
    private Long id;
    private Long dietId;
    private String dietMenu;
    private MealType mealType;

    public DietRecordResponse(DietRecord dietRecord) {
        this.id = dietRecord.getId();

        // diet가 null인지 확인하고, dietId를 설정
        if (dietRecord.getDiet() != null) {
            this.dietId = dietRecord.getDiet().getId(); // Diet의 id를 설정
            this.dietMenu = dietRecord.getDiet().getMenu();  // Diet 엔티티에서 메뉴 가져오기
        } else {
            this.dietId = null;  // diet가 null일 경우 dietId도 null
            this.dietMenu = "No menu available";  // 기본값 처리
        }

        this.mealType = dietRecord.getMealType();
    }

}
