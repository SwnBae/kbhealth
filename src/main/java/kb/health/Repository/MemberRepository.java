package kb.health.Repository;

import jakarta.persistence.EntityManager;
import kb.health.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    // 중복된 휴대폰 번호가 있는지 확인
    public boolean IsDuplicatePhoneNumber(String phoneNumber) {
        Long count = em.createQuery("select count(m) from Member m where m.phoneNumber = :phoneNumber", Long.class)
                .setParameter("phoneNumber", phoneNumber)
                .getSingleResult();
        return count > 0;
    }

    //내부 로직에서만 사용할 메서드
    public Member findById(Long id) {
        return em.find(Member.class, id);
    }

    public Optional<Member> findByMemberPN(String phoneNumber) {
        return em.createQuery("select m from Member m where m.phoneNumber = :phoneNumber", Member.class)
                .setParameter("phoneNumber", phoneNumber)
                .getResultStream()
                .findFirst();
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }
}
