package kb.health.service;

import kb.health.repository.DailyScoreRepository;
import kb.health.repository.MemberRepository;
import kb.health.repository.RecordRepository;
import kb.health.domain.DailyScore;
import kb.health.domain.Member;
import kb.health.domain.record.DietRecord;
import kb.health.domain.record.ExerciseRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScoreService {

    private final DailyScoreRepository dailyScoreRepository;
    private final MemberRepository memberRepository;
    private final RecordRepository recordRepository;

    // 모든 멤버에 대해 일일 점수를 계산하고 저장 및 총점, 10일 점수 갱신
    @Transactional
    @Scheduled(cron = "0 5 0 * * *")
    public void updateDailyScoresForAllMembers() {
        // 1. 모든 멤버를 조회
        List<Member> members = memberRepository.findAll();

        LocalDate date = LocalDate.now();

        // 2. 각 멤버에 대해 일일 점수 계산
        for (Member member : members) {
            System.out.println("=============================");
            System.out.println(member.getUserName());
            // 해당 날짜의 다이어트 기록과 운동 기록을 가져옵니다.
            List<DietRecord> dietRecordList = recordRepository.findDietRecordsByMemberAndDate(member, date);
            List<ExerciseRecord> exerciseRecordList = recordRepository.findExerciseRecordsByMemberAndDate(member, date);

            System.out.println("기록");
            for (DietRecord dietRecord : dietRecordList) {
                System.out.println(dietRecord.getDiet().getMenu());
            }

            for (ExerciseRecord exerciseRecord : exerciseRecordList) {
                System.out.println(exerciseRecord.getExerciseType());
            }

            // 3. DailyScore 생성 및 저장
            DailyScore dailyScore = DailyScore.create(member, dietRecordList, exerciseRecordList);
            System.out.println("점수");
            System.out.println(dailyScore.getTotalScore());
            dailyScoreRepository.save(dailyScore);

            // 4. 멤버의 총점과 최근 10일 점수 갱신
            updateMemberScores(member);
        }
    }

    // 테스트용
    @Transactional
    public void updateDailyScoresForAllMembers(LocalDate date) {
        // 1. 모든 멤버를 조회
        List<Member> members = memberRepository.findAll();

        // 2. 각 멤버에 대해 일일 점수 계산
        for (Member member : members) {
            System.out.println("=============================");
            System.out.println(member.getUserName());
            // 해당 날짜의 다이어트 기록과 운동 기록을 가져옵니다.
            List<DietRecord> dietRecordList = recordRepository.findDietRecordsByMemberAndDate(member, date);
            List<ExerciseRecord> exerciseRecordList = recordRepository.findExerciseRecordsByMemberAndDate(member, date);

            System.out.println("기록");
            for (DietRecord dietRecord : dietRecordList) {
                System.out.println(dietRecord.getDiet().getMenu());
            }

            for (ExerciseRecord exerciseRecord : exerciseRecordList) {
                System.out.println(exerciseRecord.getExerciseType());
            }

            if (dietRecordList.isEmpty() && exerciseRecordList.isEmpty()) {
                continue;
            }

            // 3. DailyScore 생성 및 저장
            DailyScore dailyScore = DailyScore.create(member, dietRecordList, exerciseRecordList, date);
            System.out.println("점수");
            System.out.println(dailyScore.getTotalScore());
            dailyScoreRepository.save(dailyScore);

            // 4. 멤버의 총점과 최근 10일 점수 갱신
            updateMemberScores(member);
        }
    }

    // 멤버의 총점과 최근 10일 점수를 갱신하는 메서드
    private void updateMemberScores(Member member) {
        // 1. 총점 갱신: 모든 일일 점수의 합
        double totalScore = dailyScoreRepository.sumTotalScoreByMember(member);
        totalScore = Math.floor(totalScore * 100.0) / 100.0;
        member.setTotalScore(totalScore);

        // 2. 최근 10일 점수 갱신: 최근 10일의 점수 합산
        List<DailyScore> last10Scores = dailyScoreRepository.findTop10ByMemberOrderByDateDesc(member);
        double last10DaysScore = last10Scores.stream()
                .mapToDouble(DailyScore::getTotalScore)
                .sum();

        last10DaysScore = Math.floor(last10DaysScore * 100.0) / 100.0;
        member.setBaseScore(last10DaysScore);

        // 3. 멤버 저장 (점수 갱신)
        memberRepository.save(member);
    }

    // 최근 10일간의 점수 오름차순으로 정렬해서 리스트로 반환 -> 대시보드 그래프 활용
    public List<DailyScore> getLast10DaysScores(Member member) {
        List<DailyScore> scores = dailyScoreRepository.findTop10ByMemberOrderByDateDesc(member);
        scores.sort(Comparator.comparing(DailyScore::getDate));
        return scores;
    }

}

