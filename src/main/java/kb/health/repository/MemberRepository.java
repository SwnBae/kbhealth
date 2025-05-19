package kb.health.repository;

import jakarta.persistence.EntityManager;
import kb.health.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public Long save(Member member) {
        em.persist(member);

        return member.getId();
    }

    //내부 로직에서만 사용할 메서드
    public Member findMemberById(Long id) {
        return em.find(Member.class, id);
    }

    //휴대폰 번호로 멤버 찾기
    public Optional<Member> findMemberByPN(String phoneNumber) {
        return em.createQuery("select m from Member m where m.phoneNumber = :phoneNumber", Member.class)
                .setParameter("phoneNumber", phoneNumber)
                .getResultStream()
                .findFirst();
    }

    //닉네임으로 멤버 찾기
    public Optional<Member> findMemberByName(String userName) {
        return em.createQuery("select m from Member m where m.userName = :userName", Member.class)
                .setParameter("userName", userName)
                .getResultStream()
                .findFirst();
    }

    //유저 Account로 멤버 찾기
    public Optional<Member> findMemberByAccount(String account) {
        return em.createQuery("select m from Member m where m.account = :account", Member.class)
                .setParameter("account", account)
                .getResultStream()
                .findFirst();
    }

    // 유저 검색 (Account, userName)
    public List<Member> findByUserNameOrAccountLike(String keyword) {
        return em.createQuery("select m from Member m where m.userName like :kw or m.account like :kw", Member.class)
                .setParameter("kw", "%" + keyword + "%")
                .getResultList();
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findTopByTotalScore(Pageable pageable) {
        return em.createQuery("select m from Member m order by m.totalScore desc", Member.class)
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize()) // 페이지 번호에 맞는 시작 인덱스 설정
                .setMaxResults(pageable.getPageSize()) // 한 페이지에 출력할 최대 결과 개수
                .getResultList();
    }

    public List<Member> findTopByBaseScore(Pageable pageable) {
        return em.createQuery("select m from Member m order by m.baseScore desc", Member.class)
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize()) // 페이지 번호에 맞는 시작 인덱스 설정
                .setMaxResults(pageable.getPageSize()) // 한 페이지에 출력할 최대 결과 개수
                .getResultList();
    }

    public long countMembers() {
        return em.createQuery("select count(m) from Member m", Long.class)
                .getSingleResult();
    }

    // 팔로우한 사용자들의 랭킹 (baseScore 기준)
    public List<Member> findFollowingsByBaseScore(List<Long> followingIds, Pageable pageable) {
        if (followingIds.isEmpty()) {
            return List.of(); // 팔로우한 사용자가 없으면 빈 리스트 반환
        }

        return em.createQuery("select m from Member m where m.id in :ids order by m.baseScore desc", Member.class)
                .setParameter("ids", followingIds)
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    // 팔로우한 사용자들의 총 수
    public long countFollowings(List<Long> followingIds) {
        if (followingIds.isEmpty()) {
            return 0;
        }

        return em.createQuery("select count(m) from Member m where m.id in :ids", Long.class)
                .setParameter("ids", followingIds)
                .getSingleResult();
    }

    /**
     * 점수 업데이트 ->
     */
    public List<Member> findAllOrderByTotalScoreDesc() {
        return em.createQuery("select m from Member m order by m.totalScore desc", Member.class)
                .getResultList();
    }

    public List<Member> findAllOrderByBaseScoreDesc() {
        return em.createQuery("select m from Member m order by m.baseScore desc", Member.class)
                .getResultList();
    }
}
