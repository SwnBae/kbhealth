package kb.health.repository.record;

import kb.health.domain.record.DietRecord;
import kb.health.domain.record.ExerciseRecord;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecordRepositoryCustom {
    List<DietRecord> searchDietRecordsDynamic(Long memberId, String menuKeyword, LocalDate startDate, LocalDate endDate);
    List<ExerciseRecord> searchExerciseRecordsDynamic(Long memberId, String exerciseName, LocalDate startDate, LocalDate endDate);
}
