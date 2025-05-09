package kb.health.repository;

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

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }
}
