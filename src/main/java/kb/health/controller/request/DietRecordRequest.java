package kb.health.controller.request;

import jakarta.validation.constraints.NotEmpty;
import kb.health.domain.record.MealType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 삽입, 수정 시 사용될 DTO
 */

@Getter @Setter
@NoArgsConstructor
public class DietRecordRequest {
    private Long id;

    @NotEmpty(message = "메뉴는 필수항목 입니다.")
    private Long dietId;

    @NotEmpty(message = "식사 시간은 필수항목 입니다.")
    private MealType mealType;

    public DietRecordRequest(Long dietId, MealType mealType) {
        this.dietId = dietId;
        this.mealType = mealType;
    }
}
