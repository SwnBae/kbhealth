package kb.health.repository;

import kb.health.domain.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 휴대폰 번호로 멤버 찾기
    Optional<Member> findByPhoneNumber(String phoneNumber);

    // 닉네임으로 멤버 찾기
    Optional<Member> findByUserName(String userName);

    // 유저 Account로 멤버 찾기
    Optional<Member> findByAccount(String account);

    // 유저 검색 (Account, userName) - 복합 조건이므로 @Query 사용
    @Query("SELECT m FROM Member m WHERE m.userName LIKE %:keyword% OR m.account LIKE %:keyword%")
    List<Member> findByUserNameOrAccountContaining(@Param("keyword") String keyword);

    // totalScore 기준 상위 랭킹 조회
    List<Member> findAllByOrderByTotalScoreDesc(Pageable pageable);

    // baseScore 기준 상위 랭킹 조회
    List<Member> findAllByOrderByBaseScoreDesc(Pageable pageable);

    // 팔로우한 사용자들의 랭킹 (baseScore 기준)
    @Query("SELECT m FROM Member m WHERE m.id IN :ids ORDER BY m.baseScore DESC")
    List<Member> findByIdInOrderByBaseScoreDesc(@Param("ids") List<Long> followingIds, Pageable pageable);

    // 팔로우한 사용자들의 총 수
    long countByIdIn(List<Long> followingIds);

    // 점수 업데이트용 전체 조회 메서드들
    List<Member> findAllByOrderByTotalScoreDesc();

    List<Member> findAllByOrderByBaseScoreDesc();
}