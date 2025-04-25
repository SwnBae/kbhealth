package kb.health.Service;

import kb.health.Repository.DietRepository;
import kb.health.Repository.MemberRepository;
import kb.health.Repository.RecordRepository;
import kb.health.domain.Member;
import kb.health.domain.record.*;
import kb.health.domain.request.DietRecordRequest;
import kb.health.domain.request.DietRequest;
import kb.health.domain.request.ExerciseRecordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;
    private final MemberRepository memberRepository;
    private final DietRepository dietRepository;

    /**
     * 음식 관리
     */
    @Transactional
    public Long addDiet(DietRequest dietRequest) {
        Diet diet = new Diet();
        diet.setMenu(dietRequest.getMenu());
        diet.setCalories(dietRequest.getCalories());

        return dietRepository.save(diet);
    }

    @Transactional
    public void updateDiet(Long dietId, DietRequest dietRequest) {
        Diet diet = dietRepository.findById(dietId);

        diet.setMenu(dietRequest.getMenu());
        diet.setCalories(dietRequest.getCalories());
    }

    @Transactional
    public void deleteDiet(Long dietId) {
        Diet diet = dietRepository.findById(dietId);

        if (diet != null) {
            List<DietRecord> dietRecords = recordRepository.findDietRecordsByDiet(diet);
            for (DietRecord dietRecord : dietRecords) {
                dietRecord.setDiet(null);
            }

            dietRepository.delete(dietId);
        }
    }

    public Diet getDiet(Long id){
        return dietRepository.findById(id);
    }

    public List<Diet> getDietList(){
        return dietRepository.findAll();
    }

    /**
     * Record 관리
     */

    /**
     * 저장
     */
    @Transactional
    public Long saveDietRecord(DietRecordRequest dietRecordRequest, Long memberId) {
        Member member = memberRepository.findMemberById(memberId);
        Diet diet = dietRepository.findById(dietRecordRequest.getDietId());

        DietRecord dietRecord = DietRecord.create(diet, dietRecordRequest.getMealType());
        dietRecord.assignMember(member);

        recordRepository.saveDietRecord(dietRecord);

        return dietRecord.getId();
    }

    @Transactional
    public Long saveExerciseRecord(ExerciseRecordRequest exerciseRecordRequest, Long memberId) {
        Member member = memberRepository.findMemberById(memberId);

        ExerciseRecord exerciseRecord = ExerciseRecord.create(exerciseRecordRequest);

        exerciseRecord.assignMember(member);
        recordRepository.saveExerciseRecord(exerciseRecord);

        return exerciseRecord.getId();
    }

    public List<DietRecord> getDietRecords(Long memberId) {
        return recordRepository.findDietRecordsByMember(memberId);
    }

    public List<ExerciseRecord> getExerciseRecords(Long memberId) {
        return recordRepository.findExerciseRecordsByMember(memberId);
    }

    public DietRecord getDietRecord(Long id) {
        return recordRepository.findDietRecordById(id);
    }

    public ExerciseRecord getExerciseRecord(Long id) {
        return recordRepository.findExerciseRecordById(id);
    }

    /**
     * 삭제
     */
    @Transactional
    public void deleteDietRecord(Long id) {
        DietRecord dietRecord = recordRepository.findDietRecordById(id);
        dietRecord.deleteFromMember();

        recordRepository.deleteDietRecord(id);
    }

    @Transactional
    public void deleteExerciseRecord(Long id) {
        ExerciseRecord exerciseRecord = recordRepository.findExerciseRecordById(id);
        exerciseRecord.deleteFromMember();

        recordRepository.deleteExerciseRecord(id);
    }

    /**
     * 수정
     */
    @Transactional
    public void updateDietRecord(Long id, DietRecordRequest dietRecordRequest){
        DietRecord dietRecord = recordRepository.findDietRecordById(id);
        Diet diet = dietRepository.findById(dietRecordRequest.getDietId());

        dietRecord.setDiet(diet);
        dietRecord.setMealType(dietRecordRequest.getMealType());
    }

    @Transactional
    public void updateExerciseRecord(Long id, ExerciseRecordRequest exerciseRecordRequest){
        ExerciseRecord exerciseRecord = recordRepository.findExerciseRecordById(id);

        exerciseRecord.setDurationMinutes(exerciseRecordRequest.getDurationMinutes());
        exerciseRecord.setCaloriesBurned(exerciseRecordRequest.getCaloriesBurned());
        exerciseRecord.setExerciseType(exerciseRecordRequest.getExerciseType());
    }
}
