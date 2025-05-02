package kb.health.Repository;

import kb.health.domain.DailyScore;
import kb.health.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyScoreRepository extends JpaRepository<DailyScore, Long> {

    // 특정 회원의 특정 날짜 점수 조회
    Optional<DailyScore> findByMemberAndDate(Member member, LocalDate date);

    // 최근 10일 점수 조회 (일일 점수 평균 계산 등에 사용)
    List<DailyScore> findTop10ByMemberOrderByDateDesc(Member member);

    // 특정 날짜 이전 점수 전체 조회 (스케줄러나 통계용)
    List<DailyScore> findAllByDateBefore(LocalDate date);
}

