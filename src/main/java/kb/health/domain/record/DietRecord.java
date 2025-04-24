package kb.health.domain.record;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import kb.health.domain.Member;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class DietRecord extends BaseRecord {

    private String menu;
    private int totalCalories;

    @Enumerated(EnumType.STRING)
    private MealType mealType; // 아침/점심/저녁

    /* 빌더 */
    public static DietRecord create(String menu, int totalCalories, MealType mealType, Member member) {
        DietRecord record = new DietRecord();
        record.setMenu(menu);
        record.setTotalCalories(totalCalories);
        record.setMealType(mealType);

        record.assignMember(member);

        return record;
    }

    /* 연관관계 편의 메서드 */
    private void assignMember(Member member) {
        this.setMember(member);
        member.getDietRecords().add(this); // 만약 따로 List<DietRecord>가 있다면!
    }
}

