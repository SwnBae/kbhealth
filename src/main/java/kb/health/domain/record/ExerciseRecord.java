package kb.health.domain.record;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import kb.health.domain.Member;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class ExerciseRecord extends BaseRecord {

    private int durationMinutes;
    private int caloriesBurned;

    @Enumerated(EnumType.STRING)
    private ExerciseType exerciseType;

    /* 빌더 */
    public static ExerciseRecord create(int durationMinutes, int caloriesBurned, ExerciseType exerciseType) {
        ExerciseRecord record = new ExerciseRecord();
        record.setDurationMinutes(durationMinutes);
        record.setCaloriesBurned(caloriesBurned);
        record.setExerciseType(exerciseType);

        return record;
    }

    /* 연관관계 편의 메서드 */
    public void assignMember(Member member) {
        this.setMember(member);
        member.getExerciseRecords().add(this);
    }

    public void deleteFromMember() {
        if (this.getMember() != null) {
            this.getMember().getExerciseRecords().remove(this);
            this.setMember(null);
        }
    }
}
