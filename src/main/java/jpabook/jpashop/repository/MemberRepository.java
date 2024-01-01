package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository //스프링 빈으로 등록. 컴포넌트 스캔의 대상.
@RequiredArgsConstructor
public class MemberRepository {

    //@PersistenceContext //스프링이 엔티티매니저를 생성해서 주입시켜준다.
    private final EntityManager em;

    public void save(Member member){
        em.persist(member); //jpa가 member를 저장.
    }

    public Member findOne(Long id) { //단건조회. 타입, pk
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class) //jpql이 첫번째 인자. 반환타입이 두번째 인자.
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
