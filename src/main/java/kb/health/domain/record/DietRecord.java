package kb.health.domain.record;

import jakarta.persistence.*;
import kb.health.domain.Member;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class DietRecord extends BaseRecord {

    @Id @GeneratedValue
    @Column(name = "diet_record_id")
    private Long id;

    /**
     * NULL이 될 수도 있음, 추후 처리 필요 (CASCADE? OR NULL처리?) -> 우선 NULL처리..
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diet_id")
    private Diet diet;

    @Enumerated(EnumType.STRING)
    private MealType mealType; // 아침/점심/저녁/간식

    /* 빌더 */
    public static DietRecord create(Diet diet, MealType mealType) {
        DietRecord record = new DietRecord();
        record.setDiet(diet);
        record.setMealType(mealType);

        return record;
    }

    /* 연관관계 편의 메서드 */
    public void assignMember(Member member) {
        this.setMember(member);
        member.getDietRecords().add(this); // 만약 따로 List<DietRecord>가 있다면!
    }

    public void deleteFromMember() {
        if (this.getMember() != null) {
            this.getMember().getDietRecords().remove(this);
            this.setMember(null);
        }
    }

}

